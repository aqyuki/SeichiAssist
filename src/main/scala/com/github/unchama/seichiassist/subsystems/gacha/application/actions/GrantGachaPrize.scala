package com.github.unchama.seichiassist.subsystems.gacha.application.actions

import cats.Monad
import cats.data.Kleisli
import com.github.unchama.seichiassist.subsystems.gacha.domain.GrantState
import com.github.unchama.seichiassist.subsystems.gacha.domain.gachaprize.GachaPrize
import org.bukkit.entity.Player

trait GrantGachaPrize[F[_], ItemStack] {

  import cats.implicits._

  implicit val F: Monad[F]

  def tryInsertIntoMineStack(
    prize: GachaPrize[ItemStack],
    ownerName: String
  ): Kleisli[F, Player, Boolean]

  def insertIntoPlayerInventoryOrDrop(
    prize: GachaPrize[ItemStack],
    ownerName: String
  ): Kleisli[F, Player, GrantState]

  final def grantGachaPrize(
    prize: GachaPrize[ItemStack],
    ownerName: String
  ): Kleisli[F, Player, GrantState] =
    Kleisli { player =>
      for {
        insertMineStackResult <- tryInsertIntoMineStack(prize, ownerName)(player)
        grantState <-
          if (insertMineStackResult) {
            F.pure(GrantState.GrantedMineStack)
          } else {
            insertIntoPlayerInventoryOrDrop(prize, ownerName)(player)
          }
      } yield grantState
    }

}
