(ns mario.views
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [clojure.string :refer [capitalize]]))

(defn- result-of [builds] (:build/result (first builds)))
(defn- activity [builds]
  (if (or (empty? builds) (result-of builds))
    "Sleeping"
    "Building"))
(defn- last-build-status [builds]
  (capitalize (or (result-of builds) "unknown")))

(defn- ccproject [job]
  [:Project {:name (:job/name job)
             :activity (activity (:job/builds job))
             :lastBuildStatus (last-build-status (:job/builds job))}])

(defn- xml [& args] (html (xml-declaration "utf-8") args))
(defn cctray [jobs] (xml [:Projects (map ccproject jobs)]))

