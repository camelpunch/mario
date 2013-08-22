(ns mario.url-helpers
  (:require [environ.core :refer [env]]
            [clojure.string :refer [join]]))

(def ^:private base-uri (env :site-base-uri))

(defn job-path
  ([job-name] (join "/" [nil "jobs" job-name]))
  ([base job-name] (join "/" [base "jobs" job-name])))

(defn job-url [job-name] (job-path base-uri job-name))

(defn builds-path [& args]
  (join "/" [(apply job-path args) "builds"]))

(defn builds-url [job-name]
  (builds-path base-uri job-name))

(defn build-url [job-name build-index]
  (join "/" [(builds-path base-uri job-name) build-index]))

(defn build-failure-url [job-name build-index]
  (join "/" [(builds-path base-uri job-name) build-index "failure"]))

