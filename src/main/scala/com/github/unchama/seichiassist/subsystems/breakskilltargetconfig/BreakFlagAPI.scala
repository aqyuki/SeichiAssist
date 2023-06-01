package com.github.unchama.seichiassist.subsystems.breakskilltargetconfig

import cats.data.Kleisli
import com.github.unchama.seichiassist.subsystems.breakskilltargetconfig.domain.BreakSkillTargetConfigKey

trait BreakFlagAPI[F[_], Player] {

  /**
   * @return 破壊フラグをトグルする作用
   */
  def toggleBreakFlag(breakFlagName: BreakSkillTargetConfigKey): Kleisli[F, Player, Unit]

  /**
   * @return 現在の破壊フラグを取得する作用
   */
  def breakFlag(player: Player, breakFlagName: BreakSkillTargetConfigKey): F[Boolean]

}

object BreakFlagAPI {

  def apply[F[_], Player](implicit ev: BreakFlagAPI[F, Player]): BreakFlagAPI[F, Player] = ev

}
