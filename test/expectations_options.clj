(ns expectations-options
  (:require [environ.core :refer [env]]
            [mario.db :refer [wipe]]))

(defn init-and-clear-database
  {:expectations-options :before-run}
  [] (wipe))

