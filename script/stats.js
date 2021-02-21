'use strict';

const fs = require('fs');

var myArgs = process.argv.slice(2);
//const folder = 'C:\\Users\\Andreas\\gb-data'
const folder = myArgs[0];
if (folder === undefined) {
    console.error('No argument passed');
    return;
}
let files = fs.readdirSync(folder);
//console.log(files)


function getCreateDate(str) {
    const day = parseInt(str.substr(0, 2));
    const month = parseInt(str.substr(3, 2)) - 1;
    const year = parseInt(str.substr(6, 4));
    const hh = parseInt(str.substr(11, 2)) + 1;
    const mm = parseInt(str.substr(14, 2));
    const ss = parseInt(str.substr(17, 2));
    return new Date(year, month, day, hh, mm, ss);
}

const users = {}

for (let f of files) {
    
    const path = folder + '/' + f;
    let rawdata = fs.readFileSync(path);
    let table = JSON.parse(rawdata);


    /* fs.stat('path', (err, stats) => {
        if(err) {
            throw err;
        }
    
        // print file last modified date
        console.log(`File Data Last Modified: ${stats.mtime}`);
        console.log(`File Status Last Modified: ${stats.ctime}`);
    }); */

    const stats = fs.statSync(path);
    // print file last modified date
    //console.info(path);
    const endTime = stats.mtimeMs;
    let startTime = stats.mtimeMs - 5400000;
    /* console.info('Milis ', stats.mtimeMs);
    console.info('Date ', stats.mtime); */
    if (table.created) {
        const created = getCreateDate(table.created)
        startTime = created.getTime()
        /* console.info(getCreateDate(table.created));
        console.info(table.created); */
    } else {
        //console.info('No created field');
    }
    const playTime = endTime - startTime;
    


    for (let p of table.players) {
        //console.info(p.playerid + ' -> ' + p.name)
        if (users[p.playerid] == undefined) {
            users[p.playerid] = {
                names: {

                },
                games: 0,
                time: 0
            }
        }
        if (users[p.playerid].names[p.name] == undefined) {
            users[p.playerid].names[p.name] = 0;
        }
        users[p.playerid].names[p.name] += 1;
        users[p.playerid].games += 1;
        users[p.playerid].time += playTime;
    }
}

//console.info(users)
const userlist = []
for (let pid in users) {
    //console.info(users[pid])
    userlist.push({
        name: users[pid].names,
        games: users[pid].games,
        minutes: Math.floor(users[pid].time / 60000),
        rate: Math.round((users[pid].games / files.length) * 10000) / 100
    })
}

userlist.sort((a,b) => b.games - a.games)

console.info('players:', userlist.length)
console.info(userlist)