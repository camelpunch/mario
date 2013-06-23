(ns acceptance-test
  (:import java.io.ByteArrayInputStream)
  (:use expectations
        ring.mock.request
        mario.routes)
  (:require clojure.xml))

(defn GET [path] (app (request :get path)))
(defn PUT [path body-contents] (app (body
                                      (request :put path)
                                      body-contents)))

(defn string-to-stream [string]
  (ByteArrayInputStream. (.getBytes (.trim string))))

(defn parsed-xml [string]
  (clojure.xml/parse (string-to-stream string)))

(defn projects []
  (map :attrs (:content (parsed-xml (:body (GET "/cc.xml"))))))

;; can get current state of a project from CCTray XML
(expect 200 (:status (GET "/cc.xml")))
(expect ["My Project"] (map :name (projects)))
(expect "Sleeping" (:activity (first (projects))))
(expect "complete" (:lastBuildStatus (first (projects))))
(expect "2012-12-16T20:06:51-08:00" (:lastBuildTime (first (projects))))

