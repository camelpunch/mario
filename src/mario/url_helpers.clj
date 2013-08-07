(ns mario.url-helpers
  (:require [environ.core :refer [env]]
            [clojure.string :refer [join]]))

(def ^:private base-uri (env :site-base-uri))

(defn build [job-name build-name]
  (join "/" [base-uri "jobs" job-name "builds" build-name]))

