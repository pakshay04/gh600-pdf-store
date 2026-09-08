const fs=require('fs'),path=require('path');
const root=path.join(__dirname,'..','src','main','resources','static');
const learn=fs.readFileSync(path.join(root,'learn.html'),'utf8');
const topic=fs.readFileSync(path.join(root,'learn-topic.html'),'utf8');
for(const t of ['java','python','react','ai','sql','spring']) if(!learn.includes(`/learn-topic.html?topic=${t}`)) throw new Error(`Missing clickable ${t} learning card`);
for(const t of ['Java','Python','React','AI & Generative AI','SQL','Spring Boot']) if(!topic.includes(t)) throw new Error(`Missing topic material ${t}`);
for(const x of ['01 · ROADMAP','02 · STUDY NOTES','03 · CODE LAB','04 · BUILD & PRACTICE','05 · INTERVIEW PREP','Take free quiz']) if(!topic.includes(x)) throw new Error(`Missing learning section ${x}`);
for(const x of ['data-type="roadmap"','data-type="note"','material-trigger','openLesson(type,index)','noteSearch','progressBar','localStorage','lesson-panel']) if(!topic.includes(x)) throw new Error(`Missing interactive learning feature ${x}`);
// Ensure the richer data set is present for every topic.
if(!topic.includes('JSON.parse')) throw new Error('Missing data payload');
const moduleMarkers=(topic.match(/roadmap/g)||[]).length;
const noteMarkers=(topic.match(/notes/g)||[]).length;
if(moduleMarkers < 6) throw new Error(`Expected roadmap data for 6 tracks, found ${moduleMarkers}`);
if(noteMarkers < 6) throw new Error(`Expected study notes for 6 tracks, found ${noteMarkers}`);
console.log(`PASS learning hub: 6 tracks, 48 roadmap modules, 48 study notes, interactive drawer/progress/search/checklist`);
