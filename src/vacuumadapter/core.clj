(ns vacuumadapter.core
  (:use [scad-clj.model])
  (:use [scad-clj.scad]))

(defn tube
  [diameter height thickness]
  (difference
    (cylinder (/ diameter 2) height)
    (cylinder (- (/ diameter 2) thickness) (+ height 1)))
  )

(defn tri
  [diameter thickness]
  (->>
    (polygon [[0 0] [0 (- (/ thickness 2))] [(/ thickness 2) 0]])
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
    (tri d1 thickness)))

(spit "scadtest.scad",
      (write-scad (with-fn 100 (adapter 5 10 6 10 1))))
