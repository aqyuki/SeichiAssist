package com.github.unchama.contextualexecutor

import cats.data.Kleisli
import cats.effect.IO
import com.github.unchama.targetedeffect.TargetedEffect
import org.bukkit.command.CommandSender

package object builder {
  type Result[+Error, +Success] = Either[Error, Success]

  type ResponseEffectOrResult[-CS, +T] = Result[TargetedEffect[CS], T]

  type SingleArgumentParser[+Output] = String => ResponseEffectOrResult[CommandSender, Output]

  type SenderTypeValidation[+CS] = CommandSender => IO[Option[CS]]

  type CommandArgumentsParser[-CS, Args] =
    (CS, RawCommandContext) => IO[Option[PartiallyParsedArgs[Args]]]

  type ScopedContextualExecution[CS <: CommandSender, Args] =
    ParsedArgCommandContext[CS, Args] => IO[TargetedEffect[CS]]

  type ExecutionF[F[_], CS <: CommandSender, U, Args] =
    ParsedArgCommandContext[CS, Args] => F[U]

  // コンテキストから、 CS に対して実行できる作用への関数
  type ExecutionCSEffect[F[_], CS <: CommandSender, U, Args] =
    ParsedArgCommandContext[CS, Args] => Kleisli[F, CS, U]
}
