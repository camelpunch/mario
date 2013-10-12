(ns acceptance-test
  (:import [java.io File])
  (:require [expectations :refer :all]
            [ring.mock.request :as r]
            [test-helpers :as t]
            [clojure.string :as s]
            [mario.url-helpers :refer :all]
            [mario.routes :refer [app]]))

(defn- GET [path] (app (r/request :get path)))
(defn- POST [path] (app (r/request :post path)))
(defn- PUT [path & [body]] (app (-> (r/request :put path) (r/body body))))
(defn- projects [] (->> (GET "/cc.xml") :body t/parse-cctray-projects))
(defn- location-from-response [response]
  (s/replace ((response :headers) "Location") #"http://[^/]+/" "/"))

;; CCTray XML
(expect {:status 200} (in (GET "/cc.xml")))

;; shows created jobs in the feed
(expect {:activity "Sleeping"
         :lastBuildStatus "Unknown"}
        (in (let [job-name (doto (t/uuid) (#(PUT (job-url %))))]
              (t/project-status (projects) job-name))))

;; creating a job 204s
(expect {:status 204} (in (PUT (job-url (t/uuid)))))

;; triggering a build 201s
(expect {:status 201}
        (in (POST (builds-path (doto (t/uuid) (#(PUT (job-url %))))))))

;; and updates the status in the feed
(expect {:activity "Building"
         :lastBuildStatus "Unknown"}
        (in (let [job-name (t/uuid)]
              (PUT (job-url job-name))
              (POST (builds-url job-name))
              (t/project-status (projects) job-name))))

;; and actually causes the build to run
(expect "proof I ran"
        (let [job-name (t/uuid)
              temp-path (str "/tmp/mario-proof-" job-name)
              script (str "echo 'proof I ran' > " temp-path)]
          (PUT (job-url job-name) {:script script})
          (POST (builds-url job-name))
          (let [result (clojure.string/trim-newline (slurp temp-path))]
            (.delete (File. temp-path))
            result)))

;; failing a build
(expect {:status 204}
        (in (let [job-name (doto (t/uuid)
                             (#(PUT (job-url %))))
                  build-url (location-from-response
                              (POST (builds-url job-name)))]
              (PUT (str build-url "/failure")))))

(expect {:activity "Sleeping"
         :lastBuildStatus "Failure"}
        (in (let [job-name (doto (t/uuid) (#(PUT (job-url %))))
                  build-url (location-from-response
                              (POST (builds-url job-name)))]
              (PUT (str build-url "/failure"))
              (t/project-status (projects) job-name))))

;; making a build succeed
(expect {:status 204}
        (in (let [job-name (doto (t/uuid)
                             (#(PUT (job-url %))))
                  build-url (location-from-response
                              (POST (builds-url job-name)))]
              (PUT (str build-url "/success")))))

;; a new build for a job can succeed after a previous fail
(expect {:activity "Sleeping"
         :lastBuildStatus "Success"}
        (in (let [job-name (doto (t/uuid) (#(PUT (job-url %))))
                  build-url (location-from-response
                              (POST (builds-url job-name)))]
              (PUT (str build-url "/failure"))
              (PUT (str build-url "/success"))
              (t/project-status (projects) job-name))))

;; building a non-existent job 404s
(expect {:status 404} (in (POST (builds-url "poopants"))))

;; failing a non-existent job 404s
(expect {:status 404} (in (PUT (build-failure-url "poopants" 1))))

;; failing a non-existent build 404s
(expect {:status 404}
        (in (let [job-name (doto (t/uuid) (#(PUT (job-url %))))]
              (PUT (build-failure-url job-name 999)))))
