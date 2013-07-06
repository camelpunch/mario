(ns mario.actions
  (:require [mario.db :as db]))

(defn create-job [slug]
    (db/name-job slug "")
    {:status 200})
(defn build [slug]
  (if-let [job (empty? (db/job slug))]
    {:status 404}
    {:status 201
     :headers {"Location" (str "/jobs/" slug "/builds/1")}}))

