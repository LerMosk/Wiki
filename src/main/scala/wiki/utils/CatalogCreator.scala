package wiki.utils

import wiki.models.{Catalog, CatalogItem}

import scala.annotation.tailrec

object CatalogCreator {

  def createCatalog(articles: Seq[CatalogItem]): Seq[Catalog] = {
    val catalogCh: Map[String, List[String]] = articles.foldLeft(Map[String, List[String]]()) { case (children, cat) =>
      cat.category.foldLeft(children) { case (childrenInner, catName) =>
        val sub: List[String] = childrenInner.getOrElse(catName, List.empty)
        childrenInner.updated(catName, if (!catName.equals(cat.title)) cat.title :: sub else sub)
      }
    }

    val catalogItems: Map[String, Catalog] = getCatalogItems(catalogCh, Map())

    articles.collect {
      case a if a.category.isEmpty => catalogItems.getOrElse(a.title, Catalog(a.title, List.empty))
    }
  }


  @tailrec
  private def getCatalogItems(catalogCh: Map[String, List[String]], acc: Map[String, Catalog]): Map[String, Catalog] = {
    if (catalogCh.isEmpty) acc
    else {
      val (forAcc, newCat) = catalogCh.partition {
        case (_, children) => children.forall(child => catalogCh.getOrElse(child, List.empty).isEmpty || acc.contains(child))
      }

      val newAcc = forAcc.map{
        case (name, children) => name -> Catalog(name, children.map(ch => acc.getOrElse(ch, Catalog(ch, List.empty))))
      } ++ acc
      getCatalogItems(newCat, newAcc)
      }
    }


}
