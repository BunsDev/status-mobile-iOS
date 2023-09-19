(ns quo2.components.list-items.account.style
  (:require [quo2.foundations.colors :as colors]))

(defn- background-color
  [{:keys [state blur? customization-color]}]
  (cond (and (or (= state :pressed) (= state :selected)) (not blur?))
        (colors/custom-color customization-color 50 5)
        (and (or (= state :pressed) (= state :selected)) blur?)
        colors/white-opa-5
        (and (= state :active) (not blur?))
        (colors/custom-color customization-color 50 10)
        (and (= state :pressed) blur?) colors/white-opa-10
        :else :transparent))

(defn container
  [props]
  {:height             56
   :border-radius      12
   :background-color   (background-color props)
   :flex-direction     :row
   :align-items        :center
   :padding-horizontal 12
   :padding-vertical   6
   :justify-content    :space-between})

(def left-container {:flex-direction :row :align-items :center})

(defn metric-text
  [type theme]
  {:color (case type
            :balance-positive (colors/theme-colors colors/success-50 colors/success-60 theme)
            :balance-negative (colors/theme-colors colors/danger-50 colors/danger-60 theme)
            (colors/theme-colors colors/neutral-50 colors/neutral-40 theme))})

(defn dot-divider
  [type theme]
  {:width             2
   :height            2
   :border-radius     2
   :margin-horizontal 4
   :background-color  (case type
                        :balance-positive (colors/theme-colors colors/success-50-opa-40
                                                               colors/success-60-opa-40
                                                               theme)
                        :balance-negative (colors/theme-colors colors/danger-50-opa-40
                                                               colors/danger-50-opa-40
                                                               theme)
                        (colors/theme-colors colors/neutral-80-opa-40 colors/neutral-50-opa-40 theme))})

(defn arrow-icon
  [type theme]
  {:size  16
   :color (if (= type :balance-positive)
            (colors/theme-colors colors/success-50 colors/success-60 theme)
            (colors/theme-colors colors/danger-50 colors/danger-60 theme))})
