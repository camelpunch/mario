(ns acceptance-test
  (:import java.io.ByteArrayInputStream)
  (:use expectations
        ring.mock.request
        mario.routes)
  (:require clojure.xml))

(defn GET [path] (app (request :get path)))
(defn POST [path] (app (request :post path)))
(defn PUT [path] (app (request :put path)))

(defn parsed-xml [string]
  (-> string .trim .getBytes ByteArrayInputStream. clojure.xml/parse))

(defn projects []
  (map :attrs (-> (GET "/cc.xml") :body parsed-xml :content)))

(defn project [project-name]
  (first (filter #(= project-name (:name %)) (projects))))

;; can get current state of a project from CCTray XML
(expect 200 (:status (GET "/cc.xml")))
(expect "Sleeping" (:activity (project "Successful Project")))
(expect "Success" (:lastBuildStatus (project "Successful Project")))
(expect "2012-12-16T20:06:51-08:00" (:lastBuildTime (project "Successful Project")))

;; triggering a build gives 201 (it's created, but not finished)
(expect 201 (:status (POST "/jobs/failing-project/builds")))

;; triggering build of a project that fails
(expect "Sleeping" (:activity (project "Failing Project")))

(def requested-build (POST "/jobs/failing-project/builds"))
(def build-url (get (:headers requested-build) "Location"))
(expect 201 (:status requested-build))
(expect #"/jobs/failing-project/builds/[^/]+" build-url)

