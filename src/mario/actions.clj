(ns mario.actions
  (:require [mario.views :as views]
            [mario.db :as db]))

(defn cctray []
  {:status 200
   :body (views/cctray (db/all-jobs))})

(defn create-job [name]
  (db/name-job name)
  {:status 200})

(defn build [name]
  (if-let [job (empty? (db/item :job/name name))]
    {:status 404}
    (do
      (db/build-started name)
      {:status 201
       :headers {"Location" (str "/jobs/" name "/builds/1")}})))

