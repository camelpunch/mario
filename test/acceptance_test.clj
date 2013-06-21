(ns acceptance-test
  (:import java.io.ByteArrayInputStream)
  (:use expectations
        ring.mock.request
        mario.routes)
  (:require clojure.xml))

(defn string-to-stream [string]
  (ByteArrayInputStream. (.getBytes (.trim string))))

(defn parsed-xml [string]
  (clojure.xml/parse (string-to-stream string)))

(defn body-from-get [path]
  (:body (app (request :get "/"))))

(defn ccmenu-from-get [path]
  (parsed-xml (body-from-get path)))

(defn projects-from-get [path]
  (:content (ccmenu-from-get path)))

(expect 200 (:status (app (request :get "/"))))
(expect "My Project" (:name (:attrs (first (projects-from-get "/")))))

