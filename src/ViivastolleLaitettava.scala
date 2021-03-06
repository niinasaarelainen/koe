import scala.collection.mutable.Buffer
import scala.collection.mutable.Map


trait ViivastolleLaitettava {  
  
   var viivasto = Buffer[String]()   
   val y = Map("lyr" -> 16, "alatila" ->15, "c1" -> 14, "d1" -> 13,  "e1" -> 12,  "f1" -> 11,  "g1"-> 10,  "a1"->9,  "h1" -> 8, "c2" -> 7, "d2" -> 6,  "e2" -> 5,  "f2" -> 4,  "g2"-> 3,  "ylatila1"-> 2, "ylatila2" -> 1, "ylatila3" -> 0)

   def kuva: Buffer[String]   
   def kuvanLeveys: Int   
   def pituus : Double
   def soiva: Boolean    // tarvitaan tieto sanoja laittaessa, tauko= false
  
  
   def piirraTyhjaViivasto(pituus: Int) = {
    
      var viiva = ""
      for ( i <- 1 to pituus) viiva += "-"  // muodostetaan oikean mittainen viiva
      var vali = ""
      for ( i <- 1 to pituus) vali += " "   // muodostetaan oikean mittainen väli
    
      for ( i <- 1 to 3) viivasto += vali      // ylös tyhjää varsia varten   
      for(i <- 1 to 5){
        viivasto += vali      // ylin paikka on g2 = Map index 3
        viivasto += viiva      // 5 viivaa
      } 
      for ( i <- 1 to 4) viivasto += vali       // d1, c1, alavali & sanoille tila
        
    viivasto      
  }
}
  
class Sointu(aanet: Buffer[ViivastolleLaitettava]) extends ViivastolleLaitettava{
     def nuotit = aanet   // korkeuden joutuu laskemaan jokaiselle nuotille erikseen
     def soiva =  true
     def pituus = aanet(0).pituus   // kaikkien soinnun sävelten tulee olla samanpituisia 
     def kuvanLeveys =  aanet(0).kuvanLeveys     
     var korkeudet = Buffer[Int]()
     
    def kuva = {              
        
      viivasto = piirraTyhjaViivasto(kuvanLeveys)
      for (aani <- aanet){
         val nimiMapissa = aani.asInstanceOf[Nuotti].nimiMapissa 
         val etumerkki = aani.asInstanceOf[Nuotti].etumerkki
         val extraetumerkki = aani.asInstanceOf[Nuotti].extraetumerkkiDef
         val nuppi = aani.asInstanceOf[Nuotti].nuppi
         korkeudet += y(nimiMapissa)
         if(nimiMapissa == "c1")  viivasto(y("c1")) = viivasto(y("c1")).substring(0, 1) + "--" +  viivasto(y("c1")).substring(4, 6) + "--" + viivasto(y("c1")).substring(7)         
         
         if(etumerkki.size == 0 && extraetumerkki.size == 0)  // ei etumerkkiä
            viivasto(y(nimiMapissa)) = viivasto(y(nimiMapissa)).substring(0, 3) + nuppi + viivasto(y(nimiMapissa)).substring(5)  
         else
            viivasto(y(nimiMapissa)) = viivasto(y(nimiMapissa)).substring(0, 2) + extraetumerkki + etumerkki + nuppi + viivasto(y(nimiMapissa)).substring(5)  
         if(aani.pituus== 1.5 || aani.pituus == 3)
            viivasto(y(nimiMapissa)) = viivasto(y(nimiMapissa)).substring(0, 5) + "." + viivasto(y(nimiMapissa)).substring(6)  

      }  // end for
     
      var ylospain = true
      if (korkeudet.min - 0 < 15 - korkeudet.max )   // 0 on ylin piirtoindeksi, 15 alin, lasketaan missä on enemmän tilaa
          ylospain = false
      if (pituus < 4)   // kokonuottiin ei vartta 
          piirraVarsiJaMahdollisestiVaka(korkeudet.min, korkeudet.max, ylospain)
      viivasto          
    }
     
     def piirraVarsiJaMahdollisestiVaka(mista: Int, mihin:Int ,ylospain:Boolean) = {                                  ///////// @ Sointu
            if(ylospain){
               for (i <- 1 to mihin-mista+3)   // nuottien väli + kolmen mittainen ylimenevä osuus
                 if(!korkeudet.contains(mihin-i))  // nuppien kohdalle ei vartta
                   viivasto(mihin-i) = viivasto(mihin-i).substring(0, 4) + "|" + viivasto(mihin-i).substring(5)  
            if (pituus == 0.5) viivasto(mista-3) =  viivasto(mista-3).substring(0, 5) + "\\" + viivasto(mista-3).substring(6)       
            } else {
              for (i <-  1 to mihin-mista +3)
                  if(!korkeudet.contains(mista+i))
                     viivasto(mista+i) = viivasto(mista+i).substring(0, 3) + "|" + viivasto(mista+i).substring(4 )  
              if (pituus == 0.5) viivasto(mihin + 3) =  viivasto(mihin + 3).substring(0, 4) + "/" + viivasto(mihin + 3).substring(5)                
            }        
     }     
}
 

abstract class Nuotti extends ViivastolleLaitettava {
     def nuppi = "()"   
     def soiva = true
     def korkeus: String
     def nimiMapissa: String
     def etumerkki: String
     def extraetumerkkiDef: String
       
     def piirraApuviiva() = {                       
           viivasto(y("c1")) = viivasto(y("c1")).substring(0, 1) + "--" +  viivasto(y("c1")).substring(4, 6) + "--" + viivasto(y("c1")).substring(7)         
     }
  }
 
abstract class Tauko extends ViivastolleLaitettava{
     def korkeus = "c2"    
     def soiva = false
}

 
class KokoNuotti(nuotinNimi: String, extraetumerkki: String = "") extends Nuotti{    
     def korkeus = nuotinNimi
     def pituus = 4.0
     def kuvanLeveys = 20
     def nimiMapissa = nuotinNimi.filter(_ !='#').filter(_ != 'b') // esim. gb1 --> g1
     def etumerkki = if(extraetumerkki == "n") "" else if (nuotinNimi.filter(_ !='-').size == 3) nuotinNimi(1).toString else ""
     def extraetumerkkiDef = if(extraetumerkki == "n") "" else extraetumerkki
    
     def getExtraetumerkki = extraetumerkki
  
     def piirraNuppi() = { 
        if(nimiMapissa=="c1") piirraApuviiva()
        if(etumerkki.size == 0 && extraetumerkkiDef.size == 0)  // ei etumerkkiä
           viivasto(y(nimiMapissa)) = viivasto(y(nimiMapissa)).substring(0, 3) + nuppi + viivasto(y(nimiMapissa)).substring(5)  
        else
         viivasto(y(nimiMapissa)) = viivasto(y(nimiMapissa)).substring(0, 2) + etumerkki + extraetumerkkiDef + nuppi + viivasto(y(nimiMapissa)).substring(5)  
                                                                            // ikinä ei ole molempia, toinen "" 
      }
     
      def kuva = {
         viivasto = piirraTyhjaViivasto(kuvanLeveys) 
         piirraNuppi()
         viivasto
      }
}   
 

class PuoliNuotti(nuotinNimi: String, extraetumerkki: String = "") extends KokoNuotti(nuotinNimi: String, extraetumerkki: String){
    override def korkeus = nuotinNimi
    override def pituus = 2.0
    override def kuvanLeveys = 12
    
    override def kuva = {
      super.kuva
      piirraVarsi()
      viivasto
    }
    
    def piirraVarsi() = {
      if(y(nimiMapissa) >= y("h1")){  // varsi ylös  
        for (i <- 1 to 3)
           viivasto(y(nimiMapissa)-i) = viivasto(y(nimiMapissa)-i).substring(0, 4) + "|" + viivasto(y(nimiMapissa)-i).substring(5)  
      }  
      else {  // varsi alas 
        for (i <- 1 to 3)
          viivasto(y(nimiMapissa)+i) = viivasto(y(nimiMapissa)+i).substring(0, 3) + "|" + viivasto(y(nimiMapissa)+i).substring(4)  
      }
    }  
}
  

class PisteellinenPuoliNuotti(nuotinNimi: String, extraetumerkki: String = "") extends PuoliNuotti(nuotinNimi: String, extraetumerkki: String){
      override def korkeus = nuotinNimi
      override def pituus = 3.0
      override def kuvanLeveys = 16
      
      override def kuva = {
        super.kuva
        viivasto(y(nimiMapissa)) = viivasto(y(nimiMapissa)).substring(0, 5) + "." + viivasto(y(nimiMapissa)).substring(6)  
        viivasto
      }
}
   
    
class NeljasosaNuotti(nuotinNimi: String, extraetumerkki: String = "") extends PuoliNuotti(nuotinNimi: String, extraetumerkki: String){
      override def korkeus = nuotinNimi
      override def pituus = 1.0
      override def kuvanLeveys = 8
      override def nuppi = "@@"
    
}   
  

class PisteellinenNeljasosaNuotti(nuotinNimi: String, extraetumerkki: String = "") extends NeljasosaNuotti(nuotinNimi: String, extraetumerkki: String){
    override def korkeus = nuotinNimi
    override def pituus = 1.5
    override def kuvanLeveys = 8
    
    override def kuva = {
       super.kuva
       viivasto(y(nimiMapissa)) = viivasto(y(nimiMapissa)).substring(0, 5) + "." + viivasto(y(nimiMapissa)).substring(6)  
       viivasto
    }
}   
  
    
class KahdeksasosaNuotti(nuotinNimi: String, extraetumerkki: String = "") extends NeljasosaNuotti(nuotinNimi: String, extraetumerkki: String){
     override def korkeus = nuotinNimi
     override def pituus = 0.5
     override def kuvanLeveys = 7
    
     override def kuva = {
        super.kuva
        piirraVarsiJaVaka
        viivasto
     }
    
     def piirraVarsiJaVaka = {
         if(y(nimiMapissa) >= y("h1")){  // varsi ylös  
            for (i <- 1 to 3)
               viivasto(y(nimiMapissa)-i) = viivasto(y(nimiMapissa)-i).substring(0, 4) + "|" + viivasto(y(nimiMapissa)-i).substring(5)  
            viivasto(y(nimiMapissa)-3) = viivasto(y(nimiMapissa)-3).substring(0, 5) + "\\" + viivasto(y(nimiMapissa)-3).substring(6)  
         } else {  // varsi alas 
            for (i <- 1 to 3)
                viivasto(y(nimiMapissa)+i) = viivasto(y(nimiMapissa)+i).substring(0, 3) + "|" + viivasto(y(nimiMapissa)+i).substring(4)  
            viivasto(y(nimiMapissa)+3) = viivasto(y(nimiMapissa)+3).substring(0, 4) + "/" + viivasto(y(nimiMapissa)+3).substring(5)  
         }
     } 
}   
  
  
class KahdeksasosaPari (ekaNuotti: KahdeksasosaNuotti, tokaNuotti: KahdeksasosaNuotti)  extends KokoNuotti(ekaNuotti.korkeus: String, ekaNuotti.getExtraetumerkki: String){

// (ekaNuotti: ViivastolleLaitettava, tokaNuotti: ViivastolleLaitettava)  extends Nuotti{
  

     override def korkeus = ekaNuotti.korkeus
     def korkeus2 = tokaNuotti.korkeus
     override def pituus = 1.0
     override def kuvanLeveys = 12
     override def nuppi = "@@"
     var ekanVarrenPit, tokanVarrenPit = 0
     var ylospain = true
     
     val korkeudet = Array( y(ekaNuotti.nimiMapissa),  y(tokaNuotti.nimiMapissa))
    
     if (korkeudet.min - 0 < 15 - korkeudet.max ) {  // 0 on ylin piirtoindeksi, 15 alin, lasketaan missä on enemmän tilaa
         ylospain = false  
         ekanVarrenPit = Math.abs(korkeudet.max - y(ekaNuotti.nimiMapissa)) + 2   // varren pituus on väh. 2
         tokanVarrenPit = Math.abs(korkeudet.max - y(tokaNuotti.nimiMapissa))  + 2  
        
     } else {
         ekanVarrenPit = Math.abs(korkeudet.min - y(ekaNuotti.nimiMapissa)) + 2
         tokanVarrenPit = Math.abs(korkeudet.min - y(tokaNuotti.nimiMapissa))  + 2  
     }
       
     override def kuva = {
       viivasto = piirraTyhjaViivasto(kuvanLeveys)
       super.kuva     // piirtää ekan nuotin nupin 
       // tokan nuppi:
       if(tokaNuotti.nimiMapissa == "c1")        
           viivasto(y("c1")) = viivasto(y("c1")).substring(0, 6) + "--" +  viivasto(y("c1")).substring(8, 10) + "--" + viivasto(y("c1")).substring(11)         
  
       if(tokaNuotti.etumerkki.size == 0 && tokaNuotti.extraetumerkkiDef.size == 0)  // ei etumerkkiä
            viivasto(y(tokaNuotti.nimiMapissa)) = viivasto(y(tokaNuotti.nimiMapissa)).substring(0, 8) + tokaNuotti.nuppi + viivasto(y(tokaNuotti.nimiMapissa)).substring(10)  
       else
           viivasto(y(tokaNuotti.nimiMapissa)) = viivasto(y(tokaNuotti.nimiMapissa)).substring(0, 7) + tokaNuotti.extraetumerkkiDef + tokaNuotti.etumerkki + tokaNuotti.nuppi + viivasto(y(tokaNuotti.nimiMapissa)).substring(10)  
     
       // varret ja palkki:    
       if(ylospain)  {           
          for (i <- 1 to ekanVarrenPit)  
                   viivasto( y(ekaNuotti.nimiMapissa)-i) = viivasto( y(ekaNuotti.nimiMapissa)-i).substring(0, 4) + "|" + viivasto( y(ekaNuotti.nimiMapissa)-i).substring(5)  
        
          for(i<- 1 to tokanVarrenPit) viivasto(y(tokaNuotti.nimiMapissa)-i) = viivasto(y(tokaNuotti.nimiMapissa)-i).substring(0, 9) + "|"  + viivasto(y(tokaNuotti.nimiMapissa)-i).substring(10)    
          viivasto(korkeudet.min -3) = viivasto(korkeudet.min -3).substring(0, 4) + "======"  + viivasto(korkeudet.min -3).substring(10)    
       } else { 
         // varret alaspäin
          for (i <- 1 to ekanVarrenPit)   
                   viivasto( y(ekaNuotti.nimiMapissa)+i) = viivasto( y(ekaNuotti.nimiMapissa)+i).substring(0, 3) + "|" + viivasto( y(ekaNuotti.nimiMapissa)+i).substring(4)  
        
          for(i<- 1 to tokanVarrenPit) viivasto(y(tokaNuotti.nimiMapissa)+i) = viivasto(y(tokaNuotti.nimiMapissa)+i).substring(0, 8) + "|"  + viivasto(y(tokaNuotti.nimiMapissa)+i).substring(9)    
          viivasto(korkeudet.max +3) = viivasto(korkeudet.max +3).substring(0, 3) + "======"  + viivasto(korkeudet.max +3).substring(9)    
       }  
       viivasto   
     }
    
}
 
  
class NeljasosaTauko extends Tauko {   
    def pituus = 1.0
    def kuvanLeveys = 7    
    
    def kuva = {                                    // korkeus on pelkkä piirtokorkeus
      viivasto = piirraTyhjaViivasto(kuvanLeveys) 
      viivasto(y(korkeus)) = viivasto(y(korkeus)).substring(0, 3) + "\\" +  viivasto(y(korkeus)).substring(4, kuvanLeveys)
      viivasto(y(korkeus)+1) = viivasto(y(korkeus)+1).substring(0, 3) + "/" +  viivasto(y(korkeus)+1).substring(4, kuvanLeveys)
      viivasto(y(korkeus)+2) = viivasto(y(korkeus)+2).substring(0, 3) + "\\" +  viivasto(y(korkeus)+2).substring(4, kuvanLeveys)
      viivasto(y(korkeus)+3) = viivasto(y(korkeus)+3).substring(0, 3) + "/" +  viivasto(y(korkeus)+3).substring(4, kuvanLeveys)
      viivasto 
    }
}   


class PisteellinenNeljasosaTauko extends NeljasosaTauko{      
      override def pituus = 1.5
      override def kuvanLeveys = 9
    
      override def kuva = {
        super.kuva  
        viivasto(y(korkeus)+3) = viivasto(y(korkeus)+3).substring(0, 4) + "." + viivasto(y(korkeus)+3).substring(5, kuvanLeveys)  
        viivasto   
      } 
}


class KahdeksasosaTauko extends NeljasosaTauko{      
      override def pituus = 0.5
      override def kuvanLeveys = 6
    
      override def kuva = {
         viivasto = piirraTyhjaViivasto(kuvanLeveys) 
         viivasto(y(korkeus)) = viivasto(y(korkeus)).substring(0, 2) + "/|" + viivasto(y(korkeus)).substring(4, kuvanLeveys)
         viivasto(y(korkeus)+1) = viivasto(y(korkeus)+1).substring(0, 3) + "|" + viivasto(y(korkeus)+1).substring(4, kuvanLeveys)
       viivasto   
      }  
}
  