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
(expect {:build/name 1}
        (let [uuid (first (job-uuids))]
          (build-started uuid)
          (first (:job/builds (job uuid)))))

;; can notify about build failure and retrieve the failure from single job
(expect {:build/name 1
         :build/result "failure"}
        (let [uuid (first (job-uuids))
              build-id (build-started uuid)]
          (build-failed uuid (str build-id))
          (last (:job/builds (job uuid)))))

;; or from all jobs
(expect "failure"
        (let [uuid (first (job-uuids))
              build-id (build-started uuid)]
          (build-failed uuid build-id)
          (:build/result (first (:job/builds
                                  (first
                                    (filter #(= uuid (:job/name %)) (all-jobs))))))))

;; we get nil when failing a non-existent job
(expect nil (build-failed "nonexistentjob" 0))

;; or a non-existent build
(expect nil (build-failed (first (job-uuids)) 999999))
