
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map

class Viivasto(nuottiData: Buffer[ViivastolleLaitettava], lyricsBuffer: Buffer[String], tahtilaji:String, kappaleenNimi:String) {
  
  var viivasto = piirraGavain   //Buffer[String]()
  
  val nuotitYAkselilla = Map("lyr" -> 16, "alatila" ->15, "c1" -> 14, "d1" -> 13,  "e1" -> 12,  "f1" -> 11,  "g1"-> 10,  "a1"->9,  "h1" -> 8, "c2" -> 7, "d2" -> 6,  "e2" -> 5,  "f2" -> 4,  "g2"-> 3,  "ylatila1"-> 2, "ylatila2" -> 1, "ylatila3" -> 0)
  
  var tahtiaMennyt = 0.0        // laskee iskuja
  var riviaMennytMontakoTahtia = 0             // halutaan max 4 tahtia riville
  var kappale = new Kappale
  this.kappale.lisaaKappaleenNimi(kappaleenNimi)
  
 
    
  def piirraNuotit() = {
    
     var lyricsInd = 0
     for (laitettava <- nuottiData) {      
                      // jos sanoja oli vähemmän kuin nuotteja, ei haluta kaataa ohjelmaa
       if(laitettava.soiva && lyricsBuffer.size != 0 && lyricsBuffer.size - lyricsInd >= 1 ){     // nuotti ja sointu => soiva= true, tauko=> soiva=false
           var tavu = ""
           // leikataan liian pitkien lyriikkatavujen loput:
           if ( lyricsBuffer(lyricsInd).size > laitettava.kuvanLeveys){
             tavu = lyricsBuffer(lyricsInd).substring(0, laitettava.kuvanLeveys)
           } else tavu = lyricsBuffer(lyricsInd)
           // keskitetään lyriikkatavuja lähemmäs nuottia:
           if (laitettava.kuvanLeveys - tavu.size < 2)
             laitettava.kuva(16) =  tavu  + laitettava.kuva(16).substring(tavu.size, laitettava.kuvanLeveys)
           else if (laitettava.kuvanLeveys - tavu.size < 4) 
              laitettava.kuva(16) = "  " + tavu  + laitettava.kuva(16).substring(tavu.size+2, laitettava.kuvanLeveys)
           else  laitettava.kuva(16) = "   " + tavu  + laitettava.kuva(16).substring(tavu.size+3, laitettava.kuvanLeveys)
           lyricsInd += 1
       }    
       liita(laitettava)
       tahtiaMennyt += laitettava.pituus
       if (tahtiaMennyt == tahtilaji.toDouble){
     //     println("laitettava: " + laitettava + ", pit:" + laitettava.pituus + "riviaMennytMontakoTahtia " +riviaMennytMontakoTahtia)
          riviaMennytMontakoTahtia += 1
          tahtiaMennyt = 0.0
          lisaaTahtiviiva()
       }
       if (riviaMennytMontakoTahtia == 2 ){      // printtaukseen 2, voisi kysyä käyttäjältä  //TODO      
           vaihdaRivi()
       }
    } // end for, kaikki nuottiData käsitelty
    
    lisaaTahtiviiva()   // biisin lopetusviiva, tulee vain vajaissa riveissä    TODO  : myös täyteen riviin    
    
    if(tahtiaMennyt != 0.0 || riviaMennytMontakoTahtia > 0 ){  // pelkkää G-avainta ei haluta mukaan kappaleeseen
        vaihdaRivi()   
        lisaaTahtiviiva()       
    }    
  }
  
  
  
   def liita(liitosOlio: ViivastolleLaitettava) = {
     for (i <- 0 until this.viivasto.size)
         viivasto(i) += liitosOlio.kuva(i)
   }
  
  
   
   def lisaaTahtiviiva() = {
       for(i<-nuotitYAkselilla("ylatila3") to nuotitYAkselilla("g2"))   
         viivasto(i) += " "                                       // tänne tyhjää, jotta mahdollisesti tänne tuleva nuotti/varsi asemoituu oikein    
       for(i<-nuotitYAkselilla("f2") to nuotitYAkselilla("e1"))   // tahtiviiva menee ylimmästä viivasta alimpaan
         viivasto(i) += "|"  
       for(i<-nuotitYAkselilla("d1") to nuotitYAkselilla("lyr"))  
         viivasto(i) += " "                                       // tänne tyhjää, jotta mahdollisesti tänne tuleva nuotti/lyriikka asemoituu oikein      
    }
   
   
   
   def vaihdaRivi() = {     
     for (rivi <- this.viivasto){
     }    
     kappale.lisaaViivasto(this.viivasto)    
     viivasto = piirraGavain 
     this.riviaMennytMontakoTahtia = 0
   }
    
 
   
  def piirraGavain= {
    var g = Buffer[String]()                  

    g +="          "
    g +="          "
    g +="          "
    g +="    |\\    " 
    g +="----|/----" 
    g +="    /     " 
    g +="---/|-----" 
    g +="  / |     " 
    g +="-|--|__---" 
    g +=" | /|  \\  " 
    g +="-\\-\\|---|-" 
    g +="  \\_|__/  " 
    g +="----|-----" 
    g +="  \\_/     " 
    g += "          "
    g += "          "
    g += "          "
   g
  }
 
 
}