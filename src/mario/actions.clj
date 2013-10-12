(ns mario.actions
  (:require [clojure.java.shell :refer [sh]]
            [mario.views :as views]
            [mario.url-helpers :as u]
            [mario.db :as db]))

(defn cctray [] {:status 200
                 :body (views/cctray (db/all-jobs))})

(defn create-job [job-name body]
  (let [script (:script body)]
    (db/add-job job-name script)
    {:status 204}))

(defn build [job-name]
  (if-let [job (db/job job-name)]
    (let [build-index (db/build-started job-name)
          script-path (str "/tmp/" (java.util.UUID/randomUUID))]

      (spit script-path (:script job))
      (.setExecutable (java.io.File. script-path) true)
      (sh script-path)

      {:status 201
       :headers {"Location" (u/build-url job-name build-index)}})
    {:status 404}))

(defn build-succeeded [job-name build-index]
  (let [successful-write (db/build-status job-name build-index "success")]
    {:status (if successful-write 204 404)}))

(defn build-failed [job-name build-index]
  (let [successful-write (db/build-status job-name build-index "failure")]
    {:status (if successful-write 204 404)}))
