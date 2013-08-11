(ns mario.query)

(def job-by-name
  '[:find ?job
    :in $ ?job-name
    :where [?job :job/name ?job-name]])

(def all-jobs
  '[:find ?job
    :where [?job :job/name]])

