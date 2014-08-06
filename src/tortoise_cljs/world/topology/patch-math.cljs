(ns world.topology.patch-math
  (:require [util.math :refer [squash]]
            [world :refer [get-patch-at]]
            [world.topology.vars :refer [min-pxcor min-pycor max-pxcor
                                         max-pycor wrap-in-x? wrap-in-y?]]))

(defn squash-4 [v mn]
  (squash v mn 1.0E-4))

;; wrap is tentatively corrected from the version found in
;;  topology.coffee (translated into clojure below).
;;  justification also below.
(defn wrap [p mn mx]
  ;; squash so that -5.500001 != 5.5
  (let [pos (squash-4 p mn)]
  (cond
    ;; use >= to consistently return -5.5 for the "seam" of the
    ;; wrapped shape -- i.e., -5.5 = 5.5, so consistently
    ;; report -5.5 in order to have equality checks work
    ;; correctly.
    (>= pos mx) (-> pos (- mx) (mod (- mx mn)) (+ mn))
    (< pos mn)  (- mx (-> (- mn pos)
                          (mod (- mx mn)))) ;; ((min - pos) % (max - min))
    :default pos)))

(defn wrap-y [y]
  (if wrap-in-y?
    (wrap y (- min-pycor 0.5) (+ max-pycor 0.5))
    y))

;; topology wraps, but so does world.getPatchAt ??

(defn wrap-x [x]
  (if wrap-in-x?
    (wrap x (- min-pxcor 0.5) (+ max-pxcor 0.5))
    x))

;; direct neighbors (eg getNeighbors4 patches)

(defn _get_patch_north [x y] (get-patch-at  x (wrap-y (inc y))))

(defn _get_patch_east  [x y] (get-patch-at (wrap-x (inc x)) y))

(defn _get_patch_south [x y] (get-patch-at  x (wrap-y (dec y))))

(defn _get_patch_west  [x y] (get-patch-at (wrap-x (dec x)) y))

;; corners

(defn _get_patch_northeast [x y]
  (get-patch-at (wrap-x (inc x)) (wrap-y (inc y))))

(defn _get_patch_southeast [x y]
  (get-patch-at (wrap-x (inc x)) (wrap-y (dec y))))

(defn _get_patch_southwest [x y]
  (get-patch-at (wrap-x (dec x)) (wrap-y (dec y))))

(defn _get_patch_northwest [x y]
  (get-patch-at (wrap-x (dec x)) (wrap-y (inc y))))

;; can memoize all the things. :D
;; perhaps will not want/need to memoize EVERYTHING,
;; but for now just everything.

;; NOTE: memoization here is not terribly useful.
;;       move to topology initialization code.

(def get-patch-north (memoize _get_patch_north))
(def get-patch-east  (memoize _get_patch_east))
(def get-patch-south (memoize _get_patch_south))
(def get-patch-west  (memoize _get_patch_west))

(def get-patch-northeast (memoize _get_patch_northeast))
(def get-patch-southeast (memoize _get_patch_southeast))
(def get-patch-southwest (memoize _get_patch_southwest))
(def get-patch-northwest (memoize _get_patch_northwest))

;; get neighbors

(defn _get_neighbors_4 [x y]
   (filter #(not= % nil)
          [(_get_patch_north x y)
           (_get_patch_east x y)
           (_get_patch_south x y)
           (_get_patch_west x y)]))

(defn _get_neighbors [x y]
  (concat
    (_get_neighbors_4 x y)
    (filter #(not= % nil)
          [(_get_patch_northeast x y)
           (_get_patch_northwest x y)
           (_get_patch_southwest x y)
           (_get_patch_southeast x y)])))


;; if the fns inside of get-neighbors are memoized,
;; I don't think there's a point to memoizing get-neighbors(?)

(defn get-neighbors-4 [x y]
  (filter #(not= % nil)
          [(get-patch-north x y)
           (get-patch-east  x y)
           (get-patch-south x y)
           (get-patch-west  x y)]))

(defn get-neighbors [x y]
  (concat
    (get-neighbors-4 x y)
    (filter #(not= % nil)
           [(get-patch-northeast x y)
            (get-patch-northwest x y)
            (get-patch-southwest x y)
            (get-patch-southeast x y)])))

;; shortest-x wraps a difference out of bounds.
;; _shortestX does not.

(defn shortest-x [x1 x2]
  (wrap-x (- x2 x1)))

(defn shortest-y [y1 y2]
  (wrap-y (- y2 y1)))

;; distances

(defn distanceXY [x1 y1 x2 y2]
  (let [a2 (.pow shim.strictmath (shortest-x x1, x2) 2)
        b2 (.pow shim.strictmath (shortest-y y1, y2) 2)]
    (.sqrt shim.strictmath (+ a2 b2))))

(defn distance [x y agent]
  (let [[ax ay] (.getCoords agent)]
    (distanceXY x y ax ay)))

;; towards

(defn towards [x1 y1 x2 y2]
  (let [dx (shortest-x x1 x2)
        dy (shortest-y y1 y2)]
    (cond
     (= dx 0) (or (and (< dy 0) 180) 0)
     (= dy 0) (or (and (< dx 0) 270) 90)
     :default (-> (- dy)
                  (.atan2 shim.strictmath dx)
                  (+ (.-PI js/Math))
                  (.toDegrees shim.strictmath)
                  (mod 360)
                  (+ 270)))))

;; midpoints

(defn midpoint-x [x1 x2]
  (wrap-x (-> (shortest-x x1 x2) (/ 2) (+ x1))))

(defn midpoint-y [y1 y2]
  (wrap-y (-> (shortest-y y1 y2) (/ 2) (+ y1))))

;; in-radius

(defn in-radius [x y agents radius]
  (filter #(<= (distance x y %) radius) agents))