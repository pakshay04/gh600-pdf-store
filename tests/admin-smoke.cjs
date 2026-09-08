const fs=require('fs'), path=require('path');
const root=path.join(__dirname,'..','src','main','resources','static');
const admin=fs.readFileSync(path.join(root,'admin.html'),'utf8');
const js=fs.readFileSync(path.join(root,'js','admin.js'),'utf8');
const store=fs.readFileSync(path.join(root,'store.html'),'utf8');
for(const x of ['id="file"','id="materials"','id="refresh"','id="upload"']) if(!admin.includes(x)) throw new Error('Missing admin control '+x);
for(const x of ['/api/pdfs/admin','/price','/file']) if(!js.includes(x)) throw new Error('Missing admin API '+x);
if(!store.includes('id="productList"')||!store.includes('/api/pdfs')) throw new Error('Store is not dynamic');
console.log('PASS admin material manager smoke');
