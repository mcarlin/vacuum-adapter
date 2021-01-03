(ns vacuumadapter.core
  (:use [scad-clj.model])
  (:use [scad-clj.scad]),
  (:require [cli-matic.core :refer [run-cmd]]))

(defn tube
  [diameter height thickness]
  (difference
    (cylinder (+ (/ diameter 2) (/ thickness 2))  height)
    (cylinder (/ diameter 2) (+ height 1)))
  )

(defn tri
  [diameter thickness]
  (->>
    (polygon [[0 0] [0 (- thickness)] [thickness 0]])
    (translate [(/ diameter 2) 0 0])
    (extrude-rotate)))

(defn adapter
  [d1 height1 d2 height2 thickness]
  (union
    (->>
      (tube d1 height1 thickness)
      (translate [0 0 (- (/ height1 2))]))
    (->>
      (tube d2 height2 thickness)
      (translate [0 0 (/ height2 2)]))
    (tri d1 (+ (/ thickness 2) (/ (- d2 d1) 2)))))

(defn generate-scad
  [entry]
  (let [{:strs [name from-dia from-len to-dia to-len thickness]} entry]
    (spit (str name ".scad")
          (write-scad (with-fn 100 (adapter to-dia to-len from-dia from-len thickness))))))

(defn generate-all
  [{:keys [config]}]
  (dorun (map generate-scad config)))

(def CONFIGURATION
  {:app         {:command     "vacuum"
                 :description "A command-line vacuum adapter scad generator"
                 :version     "0.1.0"}
   :commands    [{:command     "generate"
                  :opts        [{:option  "config"
                                 :as      "The configuration file of adapters to generate"
                                 :type    :jsonfile
                                 :require true}]
                  :runs        generate-all}]})

(defn -main
  [& args]
  (run-cmd args CONFIGURATION))