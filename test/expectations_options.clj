(ns expectations-options
  (:require [datomic.api :as d]
            [environ.core :refer [env]]))

(def ^:private uri (env :db-uri))
(def ^:private schema-tx (read-string (slurp "resources/schema/schema.edn")))

(defn init-and-clear-database
  {:expectations-options :before-run}
  []
  (d/delete-database uri)
  (d/create-database uri)
  (d/transact (d/connect uri) schema-tx))

