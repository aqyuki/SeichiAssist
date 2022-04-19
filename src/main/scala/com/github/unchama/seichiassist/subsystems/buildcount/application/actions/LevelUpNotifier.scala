package com.github.unchama.seichiassist.subsystems.buildcount.application.actions

import cats.effect.Sync
import cats.{Applicative, ~>}
import com.github.unchama.generic.Diff
import com.github.unchama.minecraft.actions.SendMinecraftMessage
import com.github.unchama.seichiassist.concurrent.PluginExecutionContexts.onMainThread
import com.github.unchama.seichiassist.effects.unfocused.BroadcastSoundEffect
import com.github.unchama.seichiassist.subsystems.buildcount.domain.explevel.{
  BuildAssistExpTable,
  BuildLevel
}
import com.github.unchama.seichiassist.util.Util
import com.github.unchama.targetedeffect.SequentialEffect
import com.github.unchama.targetedeffect.commandsender.MessageEffect
import org.bukkit.ChatColor.GOLD
import org.bukkit.Sound

/**
 * 建築レベルの変化を通知する機構。
 *
 * TODO Tagless algebraにするべきぽい
 */
case class LevelUpNotifier[F[_], Player]()(
  implicit F: Applicative[F],
  sync: Sync[F],
  send: SendMinecraftMessage[F, Player]
) {

  def notifyTo(player: Player)(diff: Diff[BuildLevel]): F[Unit] = {
    import cats.implicits._

    val Diff(oldLevel, newLevel) = diff
    if (newLevel eqv BuildAssistExpTable.maxLevel) {
      val bukkitPlayer = player.asInstanceOf[org.bukkit.entity.Player]
      Sync[F].delay {
        Util.sendMessageToEveryoneIgnoringPreference(
          s"$GOLD${bukkitPlayer.getName}の建築レベルが最大Lvに到達したよ(`･ω･´)"
        )
        SequentialEffect(
          BroadcastSoundEffect(Sound.ENTITY_ENDERDRAGON_DEATH, 1.0f, 1.2f),
          MessageEffect(s"${GOLD}最大Lvに到達したよ(`･ω･´)")
        ).apply(bukkitPlayer).unsafeRunAsyncAndForget()
      }
    } else if (oldLevel < newLevel)
      SendMinecraftMessage[F, Player].string(
        player,
        s"${GOLD}ﾑﾑｯﾚﾍﾞﾙｱｯﾌﾟ∩( ・ω・)∩【建築Lv(${oldLevel.level})→建築Lv(${newLevel.level})】"
      )
    else
      Applicative[F].unit
  }

  def mapK[G[_]: Applicative: Sync](fg: F ~> G): LevelUpNotifier[G, Player] = {
    implicit val e: SendMinecraftMessage[G, Player] = send.mapK(fg)
    new LevelUpNotifier[G, Player]()
  }

}
