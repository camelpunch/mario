(ns acceptance-test
  (:require [expectations :refer :all]
            [ring.mock.request :as r]
            [test-helpers :as t]
            [clojure.string :as s]
            [mario.url-helpers :as u]
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
        (in (let [job-name (doto (t/uuid) (#(PUT (u/job-path %))))]
              (t/project-status (projects) job-name))))

;; creating a job 204s
(expect {:status 204} (in (PUT (u/job-path (t/uuid)))))

;; triggering a build
(expect {:status 201}
        (in (POST (u/builds-path (doto (t/uuid) (#(PUT (u/job-path %))))))))

(expect {:activity "Building"
         :lastBuildStatus "Unknown"}
        (in (let [job-name (t/uuid)]
              (PUT (u/job-path job-name))
              (POST (u/builds-path job-name))
              (t/project-status (projects) job-name))))

;; failing a build
(expect {:status 204}
        (in (let [job-name (doto (t/uuid)
                             (#(PUT (u/job-path %))))
                  build-url (location-from-response
                              (POST (u/builds-path job-name)))]
              (PUT (str build-url "/failure")))))

(expect {:activity "Sleeping"
         :lastBuildStatus "Failure"}
        (in (let [job-name (doto (t/uuid) (#(PUT (u/job-path %))))
                  build-url (location-from-response
                              (POST (u/builds-path job-name)))]
              (do (PUT (str build-url "/failure"))
                  (t/project-status (projects) job-name)))))

;; building a non-existent job 404s
(expect {:status 404} (in (POST (u/builds-url "poopants"))))

;; failing a non-existent job 404s
(expect {:status 404} (in (PUT (u/build-failure-url "poopants" 1))))

;; failing a non-existent build 404s
(expect {:status 404}
        (in (let [job-name (doto (t/uuid) (#(PUT (u/job-path %))))]
              (PUT (u/build-failure-url job-name 999)))))
