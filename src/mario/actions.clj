(ns mario.actions
  (:require [mario.views :as views]
            [mario.url-helpers :as u]
            [mario.db :as db]))

(defn cctray []
  {:status 200
   :body (views/cctray (db/all-jobs))})

(defn create-job [job-name]
  (db/add job-name :job/name)
  {:status 204})

(defn build [job-name]
  (if-let [job (db/job job-name)]
    (let [build-name (str (java.util.UUID/randomUUID))]
      (db/build-started job-name build-name)
      {:status 201
       :headers {"Location" (u/build-url job-name build-name)}})
    {:status 404}))

(defn build-failed [job-name build-name]
  (let [successful-write (db/build-failed job-name build-name)]
    {:status (if successful-write 204 404)}))
