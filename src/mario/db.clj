(ns mario.db
  (:require [datomic.api :as d]
            [environ.core :refer :all]))

(def uri (env :db-uri))
(def schema-tx (read-string (slurp "db/schema.dtm")))

(defn init-db []
  (when (d/create-database uri)
    (let [conn (d/connect uri)]
      @(d/transact conn schema-tx))))

(defn name-job [s]
  (init-db)
  (let [conn (d/connect uri)]
    @(d/transact conn [[:db/add #db/id[:db.part/user] :job/name s]])))

(defn build-started [build-name]
  (init-db)
  (let [conn (d/connect uri)]
    @(d/transact conn [[:db/add #db/id[:db.part/user] :build/name build-name]])))

(defn item [k v]
  "Retrieves an entity from the database by a given key and value"
  (let [conn (d/connect uri)
        database (d/db conn)]
    (->>(d/q `[:find ~'?e
               :in ~'$ ~'?search-value
               :where [~'?e ~k ~'?search-value]]
             database v)
             ffirst
             (d/entity database))))

(defn all-jobs []
  (let [conn (d/connect uri)
        database (d/db conn)]
    (->>(d/q '[:find ?e
               :where [?e :job/name]]
             database)
             (map #(d/entity database (first %))))))

