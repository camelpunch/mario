(ns actions-test
  (:require [expectations :refer :all]
            [test-helpers :as t]
            [mario.url-helpers :as url]
            [mario.actions :as actions]
            [clojure.string :refer [split]]))

(defn- projects [] (t/parse-cctray-projects (:body (actions/cctray))))
(defn- location-header [response] ((response :headers) "Location"))
(defn- build-name-from-response [response]
  (last (split (location-header response) #"/")))

(def ^:private uuid-pattern "[a-f0-9-]{36}")

;; Creating a job with a name 200s
(expect {:status 200} (actions/create-job (t/uuid)))

;; Building

;; 201s when job exists
(expect-let [job-name (doto (t/uuid) actions/create-job)]
            201 (:status (actions/build job-name)))

;; …and returns a location header for the new build
(expect-let [job-name (doto (t/uuid) actions/create-job)]
            (re-pattern (url/build job-name uuid-pattern))
            (location-header (actions/build job-name)))

;; …and job shows up in feed
(expect-let [job-name (doto (t/uuid)
                        actions/create-job
                        actions/build)]
            {:name job-name
             :activity "Building"
             :lastBuildStatus "Unknown"}
            (t/project-status (projects) job-name))

;; …but 404s for a job that doesn't exist
(expect {:status 404} (actions/build "nosuchjob"))

;; Build failure

;; 200s when build exists
(expect-let [job-name (doto (t/uuid) actions/create-job)
             build-name (build-name-from-response (actions/build job-name))]
            {:status 200} (actions/build-failed job-name build-name))

;; …and 'changes' state of the job
(expect-let
  [job-name (doto (t/uuid)
              actions/create-job
              (#(let [build-name (build-name-from-response (actions/build %))]
                  (actions/build-failed % build-name))))]
  {:name job-name
   :activity "Sleeping"
   :lastBuildStatus "Failure"}
  (t/project-status (projects) job-name))

;; …but 404s when job doesn't exist
(expect {:status 404} (actions/build-failed "nosuchjob" "nosuchbuild"))

;; …and 404s when build doesn't exist
(expect-let [job-name (doto (t/uuid) actions/create-job)]
            {:status 404} (actions/build-failed job-name "nosuchbuild"))

