(ns db-test
  (:require [expectations :refer :all]
            [clojure.set :refer :all]
            [mario.db :refer :all]
            [test-helpers :as t]
            [clj-time.core :as ctime]))

(defn job-uuids [] (repeat (doto (t/uuid) add-job)))

;; naming allows finding
(expect-let [uuid (first (job-uuids))]
            uuid (:job/name (job uuid)))

;; and finding all
(expect-let [uuid (first (job-uuids))]
            uuid (in (seq (map :job/name (all-jobs)))))

;; we get nil when finding a name that doesn't exist
(expect nil (job "nonexistent"))

;; can add a build to a job
(expect {:build/index 1}
        (let [uuid (first (job-uuids))]
          (build-started uuid)
          (first (:job/builds (job uuid)))))

;; can notify about build status and retrieve the status from single job
(expect {:build/index 1
         :build/result "some-status"}
        (let [uuid (first (job-uuids))
              build-id (str (build-started uuid))]
          (build-status uuid build-id "some-status")
          (last (:job/builds (job uuid)))))

;; or from all jobs
(expect {:build/result "failure"}
        (in (let [uuid (first (job-uuids))
                  build-id (build-started uuid)]
              (build-status uuid build-id "failure")
              (first (:job/builds
                       (first
                         (filter #(= uuid (:job/name %)) (all-jobs))))))))

;; we get nil when changing status of a non-existent job
(expect nil (build-status "nonexistentjob" 0 "great-status"))

;; or a non-existent build
(expect nil (build-status (first (job-uuids)) 999999 "ace-status"))
