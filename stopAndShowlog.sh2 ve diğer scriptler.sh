
asbtest@asbzone2t00:/export/home/asbtest-> head *2
==> startLog.sh2 <==
wc -l /logs/asb_m1/asb_m1_app.log|awk '{print $1}' > LastLineNumber$1


==> stopAndShowlog.sh2 <==

lineNo=$(cat LastLineNumber$1)
lineNo=$(expr $lineNo + 1)
tail +$lineNo /logs/asb_m1/asb_m1_app.log|grep $1|egrep 'VAS_BPM_(UNICODE_)?SMS\|S' |sed 's/.*<TEXT>\([^<]*\)<.*/\1/g'|grep -v "Degerli musterimiz, Tivibu Cep Avea aylik paketiniz talebiniz uzerine iptal edilmis olup, bir sonraki donem yenilenmeyecektir. Iyi gunler dileriz"

rm LastLineNumber$1 


--PROMO--
prtdev1> head *2
==> startLog.sh2 <==
wc -l /javappl/kanbanPromo/log/promo.log|awk '{print $1}' > LastLineNumber$1

==> stopAndShowlog.sh2 <==

lineNo=$(cat LastLineNumber$1)
lineNo=$(expr $lineNo + 1)
tail +$lineNo /javappl/kanbanPromo/log/promo.log|grep $1|grep "SMS Sent To User"|cut -d\| -f3
#rm LastLineNumber$1
prtdev1> pwd
/javappl/promotionTest
prtdev1> 