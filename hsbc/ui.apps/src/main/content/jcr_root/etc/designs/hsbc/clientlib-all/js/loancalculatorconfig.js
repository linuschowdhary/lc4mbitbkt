var editor; // use a global for the submit and return data rendering in the examples

$(document).ready(function () {
    editor = new $.fn.dataTable.Editor({
        idSrc:"configName",
        ajax: {

            url: "/bin/servlet/loancalculatorconfig",
            type: 'POST',
            mimeType: "multipart/form-data",
            processData: false,
            contentType: false,
            data: {pagePath: document.location.pathname.split(".html")[0]},
            create: {
                type: 'POST',
                url: '/bin/servlet/loancalculatorconfig',
                contentType: 'multipart/form-data',
                processData: false,
                contentType: false,
                data: function (d) {
                    var form = document.querySelector('form');
                    var fData = new FormData(form);
                    for (var eachVal in d.data[0]) {
                        if (!fData.has(eachVal)) {
                            fData.append(eachVal, d.data[0][eachVal]);
                        }
                    }
                    fData.append("pagePath", document.location.pathname.split(".html")[0]);
                    fData.append("action", "create");
                    return fData;
                }
            },
            edit: {
                type: 'POST',
                url: '/bin/servlet/loancalculatorconfig',
                contentType: 'multipart/form-data',
                processData: false,
                contentType: false,
                data: function (d) {
                    debugger;
                    var form = document.querySelector('form');
                    var fData = new FormData(form);
                    for (var eachVal in d.data[$("form #DTE_Field_configName").val()]) {
                        if (!fData.has(eachVal)) {
                            fData.append(eachVal, d.data[$("form #DTE_Field_configName").val()][eachVal]);
                        }
                    }
                    fData.append("pagePath", document.location.pathname.split(".html")[0]);
                    fData.append("action", "edit");
                    return fData;
                }
            }
        },

        table: "#example",
        fields: [{
            label: "Configuration Name",
            name: "configName",
            type: "text",
            def: false

        }, {
            label: "Show Currency Input",
            name: "property_showCurrencyInput",
            type: "radio",
            options: [
                {label: "Yes", value: true},
                {label: "No", value: false}
            ],
            def: false
        }, {
            label: "CSV File",
            name: "csvFileUpload",
            type: "html5Upload"
        }
        ]
    });

    /* editor.on("preSubmit", function(e, data, action){
         debugger;
         for(var eachVal in data.data[0]){data[eachVal]=data.data[0][eachVal];}


         delete data.data in data
         debugger;
     });
     */
    var table = $('#example').DataTable({
        dom: "Bfrtip",
        ajax: {
            url: "/bin/servlet/loancalculatorconfig",
            type: 'GET',
            data: {pagePath: document.location.pathname.split(".html")[0]}
        },
        columns: [
            /*{data: "users.first_name"},
            {data: "users.last_name"},
            {data: "users.phone"},
            {data: "sites.name"},
            */
            {data: "configName"},
            {data: "property_showCurrencyInput"},
            {data: "csvFileUpload"}
        ],
        select: true,
        buttons: [
            {extend: "create", editor: editor},
            {extend: "edit", editor: editor},
            {extend: "remove", editor: editor}
        ]

    });
});

(function ($, DataTable) {

    if (!DataTable.ext.editorFields) {
        DataTable.ext.editorFields = {};
    }

    var Editor = DataTable.Editor;
    var _fieldTypes = DataTable.ext.editorFields;

    _fieldTypes.html5Upload = {
        create: function (conf) {
            var that = this;

            conf._enabled = true;

            // Create the elements to use for the input
            conf._input = $(
                '<div id="' + Editor.safeId(conf.id) + '">' +
                '<input class="fileUpload" type="file" name="csvFileUpload" accept="*">' +
                '</div>');

            // Use the fact that we are called in the Editor instance's scope to call
            // the API method for setting the value when needed
            $('button.inputButton', conf._input).click(function () {
                if (conf._enabled) {
                    that.set(conf.name, $(this).attr('value'));
                }
            });

            return conf._input;
        },

        get: function (conf) {
            return $('button.selected', conf._input).attr('value');
        },

        set: function (conf, val) {
            //$('button.selected', conf._input).removeClass( 'selected' );
            //$("button.inputButton[value='"+val+"']", conf._input).addClass('selected');
        }
        /*,

        enable: function ( conf ) {
            conf._enabled = true;
         //   $(conf._input).removeClass( 'disabled' );
        },

        disable: function ( conf ) {
            conf._enabled = false;
           // $(conf._input).addClass( 'disabled' );
        }*/
    };

})(jQuery, jQuery.fn.dataTable);
