src_file=$1

echo "getting all vfcmt resources ids..."
cat $src_file \
| python filter-vfcmts.py \
| tee temp/vfcmts.txt \
| python get-unique-id.py \
| tr -d '\r' \
| tee temp/vfcmts-unique-ids.txt
echo "number of vfcmts:" `cat temp/vfcmts.txt | wc -l `
echo "number of unique vfcmts:" `cat temp/vfcmts-unique-ids.txt | python get-uniques-only.py | wc -l`

read -p "Press enter to start delete"

while read -r line; do
  echo -e "\nDeleting $line ..."
  curl -sS "http://localhost:8080/sdc2/rest/v1/catalog/resources/$line" \
    -X DELETE \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -H "USER_ID: jh0003"
done <temp/vfcmts-unique-ids.txt

echo "Purging..."
curl "http://localhost:8080/sdc2/rest/v1/inactiveComponents/resource" \
-X DELETE \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "USER_ID: jh0003"
