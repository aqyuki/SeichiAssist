package com.github.unchama.seichiassist.subsystems.donate.domain

import com.github.unchama.seichiassist.seichiskill.effect.ActiveSkillPremiumEffect

import java.util.UUID

trait DonatePersistence[F[_]] {

  /**
   * プレミアムエフェクトポイントを増加させる作用
   */
  def addDonatePremiumEffectPoint(
    playerName: PlayerName,
    donatePremiumEffectPoint: DonatePremiumEffectPoint
  ): F[Unit]

  /**
   * プレミアムエフェクトポイントを使用する作用
   */
  def useDonatePremiumEffectPoint(uuid: UUID, effect: ActiveSkillPremiumEffect): F[Unit]

  /**
   * 現在のプレミアムエフェクトポイントの合計を取得する作用
   */
  def currentPremiumEffectPoints(uuid: UUID): F[DonatePremiumEffectPoint]

  /**
   * プレミアムエフェクトの購入履歴を取得する作用
   */
  def donatePremiumEffectPointPurchaseHistory(uuid: UUID): F[Vector[PremiumEffectPurchaseData]]

}
