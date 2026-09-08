const fs = require('fs');
const vm = require('vm');
const path = require('path');
const root = path.resolve(__dirname, '..');
const quizPath = path.join(root, 'src/main/resources/static/js/quiz.js');
const source = fs.readFileSync(quizPath, 'utf8');
const start = source.indexOf('const banks=');
const end = source.indexOf('let topic', start);
if (start < 0 || end < 0) throw new Error('Could not locate quiz bank');
const context = {};
vm.createContext(context);
vm.runInContext(source.slice(start, end) + ';this.banks=banks;', context);
const banks = context.banks;
const expected = ['AI','Java','Python','React','Cloud','Claude','GitHub','AWS','Microsoft','Spring'];
for (const topic of expected) {
  if (!Array.isArray(banks[topic]) || banks[topic].length !== 5) throw new Error(`${topic}: expected 5 questions`);
  banks[topic].forEach((q, i) => {
    if (!Array.isArray(q) || q.length !== 4) throw new Error(`${topic} Q${i+1}: expected [question, options, correct, explanation]`);
    if (typeof q[0] !== 'string' || !q[0].trim()) throw new Error(`${topic} Q${i+1}: missing question`);
    if (!Array.isArray(q[1]) || q[1].length !== 4) throw new Error(`${topic} Q${i+1}: expected 4 options`);
    if (!q[1].includes(q[2])) throw new Error(`${topic} Q${i+1}: correct answer not found in options`);
    if (typeof q[3] !== 'string' || !q[3].trim()) throw new Error(`${topic} Q${i+1}: missing explanation`);
  });
}
console.log(`PASS quiz data: ${expected.length} topics / ${expected.length * 5} questions`);

const staticDir = path.join(root, 'src/main/resources/static');
const pages = ['index.html','certifications.html','learn.html','quizzes.html','store.html','buy.html','contact.html','privacy.html','refund.html','terms.html','admin-login.html','admin.html','learn-topic.html','exams/gh-600/index.html'];
for (const page of pages) {
  const file = path.join(staticDir, page);
  if (!fs.existsSync(file)) throw new Error(`Missing page: ${page}`);
  const html = fs.readFileSync(file, 'utf8');
  if (!/<html/i.test(html) || !/<body/i.test(html)) throw new Error(`Malformed HTML shell: ${page}`);
}
for (const script of ['js/app.js','js/admin.js','js/quiz.js']) {
  const file = path.join(staticDir, script);
  if (!fs.existsSync(file)) throw new Error(`Missing script: ${script}`);
}
console.log(`PASS static pages: ${pages.length}`);
console.log('PASS frontend smoke tests');

// Exercise the browser-facing quiz functions with a tiny DOM stub.
const elements = {};
function el(id) {
  if (!elements[id]) elements[id] = {
    id, innerHTML:'', textContent:'', disabled:false, offsetTop:100,
    classList:{ add(){}, remove(){}, contains(){return false;} }
  };
  return elements[id];
}
elements.quizTopics = el('quizTopics');
elements.quiz = el('quiz');
elements.quizMeta = el('quizMeta');
elements.question = el('question');
elements.explanation = el('explanation');
elements.next = el('next');
elements.result = el('result');
elements.score = el('score');
const uiContext = {
  document:{ getElementById:el, querySelectorAll:()=>[0,1,2,3].map(()=>({disabled:false,style:{}})) },
  window:{scrollTo:()=>{}},
  console:{log:()=>{},error:()=>{}},
  module:{exports:{}}, exports:{}
};
vm.createContext(uiContext);
vm.runInContext(source, uiContext);
for (const topic of expected) {
  uiContext.startQuiz(topic);
  uiContext.answer(0);
  const feedback = String(elements.explanation.textContent || elements.explanation.innerHTML);
  if (feedback.includes('undefined')) throw new Error(`${topic}: feedback contains undefined`);
}
console.log('PASS quiz interaction smoke: start + answer for all topics');
for (const topic of expected) {
  uiContext.startQuiz(topic);
  for (let i = 0; i < banks[topic].length; i++) {
    const correct = banks[topic][i][1].indexOf(banks[topic][i][2]);
    uiContext.answer(correct);
    uiContext.nextQuestion();
  }
  if (String(elements.score.textContent) !== '5 / 5') throw new Error(`${topic}: all-correct score was ${elements.score.textContent}`);
}
console.log('PASS quiz scoring smoke: all topics score 5/5 when all correct');
