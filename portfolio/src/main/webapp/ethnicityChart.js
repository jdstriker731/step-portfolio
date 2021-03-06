// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Creates a chart showing my ethnicities and adds it to the page */
function drawChart() {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Ethnicity');
    data.addColumn('number', 'Percentage');
    data.addRows([
        ['Haitian', 37.5],
        ['Jamaican', 25],
        ['Dominican', 25],
        ['French', 12.5]
    ]);

    const options = {
        'title': 'Ethnicities', data,
        'width': 500,
        'height': 500
    };

    const chart = new google.visualization.PieChart(
        document.getElementById('ethnicity-chart'));
    chart.draw(data, options);
}
