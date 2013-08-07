(ns mario.db
  (:require [datomic.api :as d]
            [environ.core :refer [env]]))

(def ^:private uri (env :db-uri))
(def ^:private schema-tx (read-string (slurp "db/schema.dtm")))

(def ^:private query-job
  '[:find ?job
    :in $ ?job-name
    :where [?job :job/name ?job-name]])

(defn- init-db []
  (when (d/create-database uri)
    (let [conn (d/connect uri)]
      @(d/transact conn schema-tx))))

; (defn- database []
;   (init-db)
;   (let [conn (d/connect uri)]
;     (d/db conn)))

(defn add [value attr]
  (init-db)
  (let [conn (d/connect uri)]
    @(d/transact conn [[:db/add #db/id[:db.part/user] attr value]])))

(defn job [job-name]
  (init-db)
  (let [database (d/db (d/connect uri))]
    (when-let [job (d/entity database
                             (ffirst (d/q query-job database job-name)))]
      (d/touch job))))

(defn build-started [job-name build-name]
  "Ties a new build to a job"
  (init-db)
  (let [conn (d/connect uri)
        job-id (:db/id (job job-name))]
    @(d/transact conn [{:db/id job-id
                        :job/builds [{:build/name build-name}]}])))

(defn- job-build [job build-name]
  (first (filter #(= build-name (:build/name %)) (:job/builds job))))

(defn build-failed [job-name build-name]
  "Adds a failure result to a build"
  (init-db)
  (when-let [build-id (:db/id (job-build (job job-name) build-name))]
    @(d/transact (d/connect uri) [{:db/id build-id
                                   :build/result "failure"}])))

(defn all-jobs []
  (init-db)
  (let [database (d/db (d/connect uri))]
    (map #(d/touch (d/entity database (first %)))
         (d/q '[:find ?job
                :where [?job :job/name]]
              database))))

