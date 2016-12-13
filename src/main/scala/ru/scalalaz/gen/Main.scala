/*
 * Copyright 2016 Scalalaz Podcast Team
 *
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

package ru.scalalaz.gen

import java.nio.file.Paths

object Main extends App {

  val markdownDir   = Paths.get(getClass.getResource("/md").getPath)
  val targetPath    = Paths.get("target/site")
  val tmp           = Paths.get("target/tmp")
  val targetRssPath = Paths.get("target/site/rss")

  fs.clean(targetPath)
  fs.createDir(targetRssPath)

  val gen = new Generator(markdownDir, targetPath, tmp)
  gen.generate() match {
    case Left(error) =>
      println("Generation failed, error:")
      println(error)
      sys.exit(1)
    case _ =>
      println("Done")
  }
}
