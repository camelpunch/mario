(ns expectations-options
  (:require [datomic.api :as d]
            [environ.core :refer [env]]))

(defn clear-database
  "clears the database before each run"
  {:expectations-options :before-run}
  []
  (d/delete-database (env :db-uri)))


