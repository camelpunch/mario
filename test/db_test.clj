(ns db-test
  (:require [expectations :refer :all]
            [mario.db :as db]))

; creating allows finding
(expect "My New Job"
        (do
          (db/name-job "my-new-job" "My New Job")
          (:job/name (db/job "my-new-job"))))
