(ns mario.views
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [xml-declaration]]
            [clojure.string :refer [capitalize]]))

(defn- result [builds] (:result (first builds)))
(defn- activity [builds]
  (if (or (empty? builds) (result builds))
    "Sleeping"
    "Building"))
(defn- last-build-status [builds]
  (capitalize (or (result builds) "unknown")))

(defn- ccproject [job]
  [:Project {:name (:name job)
             :activity (activity (:builds job))
             :lastBuildStatus (last-build-status (:builds job))}])

(defn- xml [& args] (html (xml-declaration "utf-8") args))
(defn cctray [jobs] (xml [:Projects (map ccproject jobs)]))

