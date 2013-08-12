(ns mario.db
  (:require [datomic.api :as d]
            [environ.core :refer [env]]
            [mario.query :as query]))

(def ^:private uri (env :db-uri))
(def ^:private schema-tx (read-string (slurp "db/schema.dtm")))

(defn- init-db []
  (when (d/create-database uri)
    (d/transact (d/connect uri) schema-tx)))

(defn add [value attr]
  "Add arbitrary attribute and value"
  (init-db)
  (let [conn (d/connect uri)]
    (d/transact conn [[:db/add #db/id[:db.part/user] attr value]])))

(defn job [job-name]
  "Returns a job by name"
  (init-db)
  (let [database (d/db (d/connect uri))]
    (d/entity database (ffirst (d/q query/job-by-name database job-name)))))

(defn all-jobs []
  (init-db)
  (let [database (d/db (d/connect uri))]
    (map #(d/entity database (first %)) (d/q query/all-jobs database))))

(defn build-started [job-name build-name]
  "Ties a new build to a job"
  (init-db)
  (d/transact (d/connect uri)
              [{:db/id (:db/id (job job-name))
                :job/builds [{:build/name build-name}]}]))

(defn- build-from-names [job-name build-name]
  (first (filter #(= build-name (:build/name %))
                 (:job/builds (job job-name)))))

(defn build-failed [job-name build-name]
  "Adds a failure result to a build"
  (init-db)
  (when-let [build-id (:db/id (build-from-names job-name build-name))]
    (d/transact (d/connect uri) [{:db/id build-id
                                  :build/result "failure"}])))

