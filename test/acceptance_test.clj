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

;; CCTray XML feed 200s
(expect {:status 200} (in (GET "/cc.xml")))

;; and shows created jobs in the feed
(expect-let [job-name (doto (t/uuid)
                        (#(PUT (u/job-path %))))]
            {:activity "Sleeping"
             :lastBuildStatus "Unknown"}
            (in (t/project-status (projects) job-name)))

;; triggering a build puts job into Building state
(expect-let [job-name (doto (t/uuid)
                        (#(PUT (u/job-path %)))
                        (#(POST (u/builds-path %))))]
            {:activity "Building"
             :lastBuildStatus "Unknown"}
            (in (t/project-status (projects) job-name)))

;; job is a Sleeping Failure when build completed but failed
(expect-let [job-name (doto (t/uuid)
                        (#(PUT (u/job-path %))))
             build-path (location-from-response
                          (POST (u/builds-path job-name)))]
            {:activity "Sleeping"
             :lastBuildStatus "Failure"}
            (in (do (PUT (str build-path "/failure"))
                    (t/project-status (projects) job-name))))
