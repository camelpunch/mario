(ns mario.db
  (:require [datomic.api :as d]
            [environ.core :refer :all]))

(d/create-database (env :db-uri))
(def conn (d/connect (env :db-uri)))

(def schema-tx (read-string (slurp "db/schema.dtm")))
@(d/transact conn schema-tx)

(defn name-job [slug name]
  @(d/transact conn [{:db/id #db/id[:db.part/db -1000001]
                      :job/name name
                      :job/slug slug}]))

(defn job [slug]
  (let [db (d/db conn)]
    (->>(d/q '[:find ?e
               :in $ ?search-slug
               :where [?e :job/slug ?search-slug]]
             db slug)
             ffirst (d/entity db))))

