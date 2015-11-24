package io.itdraft.levenshteinautomaton.description

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.itdraft.levenshteinautomaton.description.nonparametric._
import io.itdraft.levenshteinautomaton.description.parametric.ParametricState
import io.itdraft.levenshteinautomaton.{AutomatonConfig, AutomatonConfigWithEncodedParametricDescription, DefaultAutomatonConfig}

/**
  * A class to represent the Levenshtein-automaton state.
  */
trait State {

  /**
    * Returns the max relevant subword length for this `State`.
    */
  def relevantSubwordMaxLength: Int

  /**
    * Tests if it's a failure state or not.
    */
  def isFailure: Boolean

  /**
    * Tests if it's a final state or not.
    */
  def isFinal: Boolean

  /**
    * Returns a new `State` produced by elementary transitions of this `State`'s positions.
    * @param symbolCodePoint Symbol's code point.
    */
  protected[levenshteinautomaton] def transit(symbolCodePoint: Int): State
}


object State {

  /**
    * Factory method to create an initial state with respect to `AutomatonConfig`.
    *
    * @return An instance of `NonParametricState` or `ParametricState`. It depends on
    *         automaton's configuration type.
    */
  protected[levenshteinautomaton]
  def initial()(implicit ch: AutomatonConfig): State = ch match {
    case c: DefaultAutomatonConfig => initial(c)
    case c: AutomatonConfigWithEncodedParametricDescription => initial(c)
  }

  protected[levenshteinautomaton]
  def initial(ch: DefaultAutomatonConfig): NonparametricState = {
    implicit val _ = ch
    State(0 ^# 0)
  }

  protected[levenshteinautomaton]
  def initial(ch: AutomatonConfigWithEncodedParametricDescription) =
    new ParametricState(0, ch)

  protected[levenshteinautomaton]
  def apply(positions: Position*)(implicit c: DefaultAutomatonConfig): NonparametricState =
    State(ImageSet(positions: _*))

  protected[levenshteinautomaton]
  def apply(imageSet: ImageSet)(implicit c: DefaultAutomatonConfig) =
    if (imageSet.isEmpty) FailureState
    else new StandardState(imageSet, c)

  protected[levenshteinautomaton]
  def apply(encodedState: Int, statesCount: Int)
           (implicit c: AutomatonConfigWithEncodedParametricDescription): ParametricState =
    new ParametricState(encodedState, c)
}