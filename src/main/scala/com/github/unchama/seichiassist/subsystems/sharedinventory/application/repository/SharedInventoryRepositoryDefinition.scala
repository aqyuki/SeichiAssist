package com.github.unchama.seichiassist.subsystems.sharedinventory.application.repository

import cats.Monad
import cats.effect.Sync
import cats.effect.concurrent.Ref
import com.github.unchama.datarepository.definitions.RefDictBackedRepositoryDefinition
import com.github.unchama.datarepository.template.RepositoryDefinition
import com.github.unchama.minecraft.algebra.HasUuid
import com.github.unchama.seichiassist.subsystems.sharedinventory.domain.SharedInventoryPersistence
import com.github.unchama.seichiassist.subsystems.sharedinventory.domain.bukkit.InventoryContents

object SharedInventoryRepositoryDefinition {

  case class RepositoryValue[F[_]](sharedFlag: Ref[F, InventoryContents])

  def withContext[G[_]: Sync, F[_], Player: HasUuid](
    persistence: SharedInventoryPersistence[G]
  ): RepositoryDefinition[G, Player, RepositoryValue[G]] = {
    RefDictBackedRepositoryDefinition
      .usingUuidRefDict[G, Player, InventoryContents](persistence)(InventoryContents.initial)
      .toRefRepository
      .augmentToTwoPhased((_, ref) => Sync[G].pure(RepositoryValue[G](ref)))(value =>
        Monad[G].pure(value.sharedFlag)
      )
  }

}