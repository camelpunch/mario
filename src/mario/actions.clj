(ns mario.actions)

(defn build [job-id]
  {:status 201
   :headers {"Location" (str "/jobs/" job-id "/builds/foo")}})

