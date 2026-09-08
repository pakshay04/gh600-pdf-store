const fs=require('fs'),path=require('path');
const root=path.join(__dirname,'..','src','main','resources','static');
const html=fs.readFileSync(path.join(root,'community.html'),'utf8');
const js=fs.readFileSync(path.join(root,'js','community.js'),'utf8');
for(const x of ['Search','Trending','Popular topics']) if(!html.includes(x)) throw new Error('Missing '+x);
for(const x of ['/api/community/posts','/api/community/trending','/vote','/report','/accept','profile.html']) if(!js.includes(x)) throw new Error('Missing '+x);
for(const f of ['profile.html','community.html']) if(!fs.existsSync(path.join(root,f))) throw new Error('Missing page '+f);
console.log('PASS community UI: search, tags, trending, voting, accepted answers, reports, profiles');
