(ns acceptance-test
  (:import java.io.ByteArrayInputStream)
  (:require [clojure.xml :as xml]
            [expectations :refer :all]
            [ring.mock.request :as r]
            [mario.routes :refer [app]]))

(defn GET [path] (app (r/request :get path)))
(defn POST [path] (app (r/request :post path)))
(defn PUT [path & [body]] (app (-> (r/request :put path) (r/body body))))

(defn parsed-xml [string]
  (->> string .trim .getBytes ByteArrayInputStream. xml/parse))

(defn projects []
  (->> (GET "/cc.xml") :body parsed-xml :content (map :attrs)))

(defn project-status [project-name]
  (first (filter #(= project-name (:name %)) (projects))))

;; XML feed works
(expect 200 (:status (GET "/cc.xml")))

;; can get current state of a project from CCTray XML
(expect {:activity "Sleeping"
         :lastBuildStatus "Success"
         :lastBuildTime "2012-12-16T20:06:51-08:00"}
        (in (project-status "Successful Project")))

;; triggering a build puts job into Building state
;(expect {:activity "Building"
         ;:lastBuildStatus "Unknown"}
        ;(in (do
              ;(PUT "/jobs/trigger-project" {:name "Trigger Project"})
              ;(POST "/jobs/trigger-project/builds")
              ;(project-status "Trigger Project"))))

;; when someone tells mario that the build completed but failed
;; then the job is a sleeping failure
