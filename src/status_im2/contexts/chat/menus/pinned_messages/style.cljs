(ns status-im2.contexts.chat.menus.pinned-messages.style
  (:require
    [quo2.foundations.colors :as colors]))

(defn heading
  [community?]
  {:margin-horizontal 20
   :margin-bottom     (when-not community? 24)})

(defn heading-container
  []
  {:flex-direction    :row
   :background-color  (colors/theme-colors colors/neutral-10 colors/neutral-80)
   :border-radius     20
   :align-items       :center
   :align-self        :flex-start
   :margin-horizontal 20
   :padding           4
   :margin-top        8
   :margin-bottom     16})

(defn heading-text
  []
  {:margin-left  6
   :margin-right 4
   :color        (colors/theme-colors colors/neutral-60 colors/neutral-20)})

(defn chat-name-text
  []
  {:margin-left  4
   :margin-right 8
   :color        (colors/theme-colors colors/neutral-60 colors/neutral-20)})

(defn list-footer
  [bottom-inset]
  {:height bottom-inset})

(defn no-pinned-messages-container
  [bottom-inset]
  {:justify-content :center
   :align-items     :center
   :margin-top      20
   :margin-bottom   bottom-inset})

(def no-pinned-messages-icon
  {:width           120
   :height          120
   :justify-content :center
   :align-items     :center
   :border-width    1})

(def no-pinned-messages-text
  {:margin-top 20})
