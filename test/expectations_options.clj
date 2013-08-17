(ns expectations-options)

(defn init-and-clear-database
  {:expectations-options :before-run}
  [] (mario.db/wipe))

