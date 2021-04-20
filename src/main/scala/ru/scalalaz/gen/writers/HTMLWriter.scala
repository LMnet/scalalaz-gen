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

package ru.scalalaz.gen.writers

import java.nio.file._

import ru.scalalaz.gen._

import html._

class HTMLWriter(targetDir: String, discusCode: String) {

  val pageCount = 5

  def write(episodes: Seq[EpisodeFile]): Unit = {
    val episodeUnits = episodes.map(f => {
      val fName = f.path.getFileName.toString.replace(".md", ".html")
      EpisodePage(fName, discusCode, f.episode)
    })

    episodeUnits.foreach(_.write(targetDir))

    buildMainPages(episodeUnits).foreach(_.write(targetDir))
  }

  private def buildMainPages(episodes: Seq[EpisodePage]): List[MainPage] = {
    val sorted: Seq[EpisodePage] =
      episodes.sortWith((e1, e2) => e1.fileName.compareTo(e2.fileName) > 0)

    val splittedOnPages: List[(Seq[EpisodePage], PageName)] = sorted
      .grouped(pageCount)
      .zipWithIndex
      .map({
        case (eps, i) =>
          val order = i + 1
          val file: FileName = if (i == 0) "index.html" else s"page-$order.html"
          eps -> PageName(file, order)
      })
      .toList

    val allPages: List[PageName] = splittedOnPages.map { case (_, pageName) =>
      pageName
    }

    splittedOnPages.zipWithIndex.map { case ((episodes, pageName), idx) =>
      MainPage(
        fileName = pageName.file,
        episodes = episodes,
        pagination = Pagination.from(
          currentPageIndex = idx,
          allPages = allPages,
        )
      )
    }
  }

}

case class PageName(
  file: FileName,
  order: Int,
)

case class EpisodePage(fileName: FileName, disqusCode: String, episode: Episode)
    extends PageUnit {

  override def content: String =
    episode_page(episode, disqusCode).body

}

case class MainPage(fileName: FileName,
                    episodes: Seq[EpisodePage],
                    pagination: Pagination[PageName])
    extends PageUnit {

  override def content: String =
    main_page("Scalalaz Podcast", episodes, pagination).body

}
