/*
Script: mooPrompter.js (Which is the same PBBAcpBox.js. It is only a rename)
    Contains <PBBAcpBox>
 
Author:
    Pokemon_JOJO, <http://www.mibhouse.org/pokemon_jojo>
 
License:
    MIT-style license.
 
*/
 
/*
Class: mooPrompter
    Clone class of original javascript function : 'alert', 'confirm' and 'prompt'
 
Arguments:
    options - see Options below
 
Options:
    name - name of the box for use different style
    zIndex - integer, zindex of the box
    onReturn - return value when box is closed. defaults to false
    onReturnFunction - a function to fire when return box value
    BoxStyles - stylesheets of the box
    OverlayStyles - stylesheets of overlay
    showDuration - duration of the box transition when showing (defaults to 200 ms)
    showEffect - transitions, to be used when showing
    closeDuration - Duration of the box transition when closing (defaults to 100 ms)
    closeEffect - transitions, to be used when closing
    onShowStart - a function to fire when box start to showing
    onCloseStart - a function to fire when box start to closing
    onShowComplete - a function to fire when box done showing
    onCloseComplete - a function to fire when box done closing
*/
var mooPrompter = new Class({
 
    getOptions: function(){
        return {
            name: 'PBBAcp',
            zIndex: 65555,
            onReturn: false,
            onReturnFunction : Class.empty,
            BoxStyles: {
                'width': 500
            },
            OverlayStyles: {
                'background-color': '#000',
                'opacity': 0.7
            },
            showDuration: 200,
            showEffect: Fx.Transitions.linear,
            closeDuration: 100,
            closeEffect: Fx.Transitions.linear,
            moveDuration: 500,
            moveEffect: Fx.Transitions.backOut,
            onShowStart : Class.empty,
            onShowComplete : Class.empty,
            onCloseStart : Class.empty,
            onCloseComplete : function(properties) {
                this.options.onReturnFunction(this.options.onReturn);
            }.bind(this)
        };
    },
 
    initialize: function(options){
        this.setOptions(this.getOptions(), options);
 
        // création de l'overlay
        this.Overlay = new Element('div', {
            'id': 'BoxOverlay',
            'styles': {
                'display': 'none',
                'z-index': this.options.zIndex,
                'position': 'absolute',
                'top': '0',
                'left': '0',
                'background-color': this.options.OverlayStyles['background-color'],
                'opacity': 0,
                'height': window.getScrollHeight() + 'px',
                'width': window.getScrollWidth() + 'px'
            }
        });
 
        this.Content = new Element('div', {
            'id': this.options.name + '-BoxContent'
        });
 
        this.InBox = new Element('div', {
            'id': this.options.name + '-InBox'
        }).adopt(this.Content);;
 
        this.Box = new Element('div', {
            'id': this.options.name + '-Box',
            'styles': {
                'display': 'none',
                'z-index': this.options.zIndex + 2,
                'position': 'absolute',
                'top': '0',
                'left': '0',
                'width': this.options.BoxStyles['width'] + 'px'
            }
        }).adopt(this.InBox);
 
        this.Overlay.injectInside(document.body);
        this.Box.injectInside(document.body);
 
        // Si le navigateur est redimentionné
        window.addEvent('resize', function() {
            if(this.options.display == 1) {
                this.Overlay.setStyles({
                    'height': window.getScrollHeight() + 'px',
                    'width': window.getScrollWidth() + 'px'
                });
                this.replaceBox();
            }
        }.bind(this));
 
        window.addEvent('scroll', this.replaceBox.bind(this));
    },
 
    /*
    Property: display
        Show or close box
 
    Argument:
        option - integer, 1 to Show box and 0 to close box (with a transition).
    */  
    display: function(option){
        // Stop la transition en action si elle existe  
        if(this.Transition)
            this.Transition.stop();             
 
        // Show Box 
        if(this.options.display == 0 && option != 0 || option == 1) {
            this.Overlay.setStyle('display', 'block');
            this.options.display = 1;
            this.fireEvent('onShowStart', [this.Overlay]);
 
            // Nouvelle transition      
            this.Transition = this.Overlay.effect(
                'opacity', 
                {
                    duration: this.options.showDuration,
                    transition: this.options.showEffect,
                    onComplete: function() {
                        sizes = window.getSize();
                        this.Box.setStyles({
                            'display': 'block',
                            'left': (sizes.scroll.x + (sizes.size.x - 
this.options.BoxStyles['width']) / 2).toInt()
                        });
                        this.replaceBox();
                        this.fireEvent('onShowComplete', [this.Overlay]);
                    }.bind(this)
                }
            ).start(this.options.OverlayStyles['opacity']);
        }
        // Close Box
        else {
            this.Box.setStyles({
                'display': 'none',
                'top': 0
            });
            this.Content.empty();
            this.options.display = 0;
 
            this.fireEvent('onCloseStart', [this.Overlay]);
 
            // Nouvelle transition      
            this.Transition = this.Overlay.effect(
                'opacity',
                {
                    duration: this.options.closeDuration,
                    transition: this.options.closeEffect,
                    onComplete: function() {
                        this.fireEvent('onCloseComplete', [this.Overlay]);
                    }.bind(this)
                }
            ).start(0);
        }           
    },
 
    /*
    Property: replaceBox
        Move Box in screen center when brower is resize or scroll
    */
    replaceBox: function() {
        if(this.options.display == 1) {
            sizes = window.getSize();
 
            if(this.MoveBox)
                this.MoveBox.stop();
 
            this.MoveBox = this.Box.effects({
                duration: this.options.moveDuration,
                transition: this.options.moveEffect
            }).start({
                'left': (sizes.scroll.x + (sizes.size.x - 
this.options.BoxStyles['width']) / 2).toInt(),
                'top': (sizes.scroll.y + (sizes.size.y - this.Box.offsetHeight) / 
2).toInt()
            });
        }
    },
 
    /*
    Property: messageBox
        Core system for show all type of box
 
    Argument:
        type - string, 'alert' or 'confirm' or 'prompt'
        message - text to show in the box
        properties - see Options below
        input - text value of default 'input' when prompt
 
    Options:
        textBoxBtnOk - text value of 'Ok' button
        textBoxBtnCancel - text value of 'Cancel' button
        onComplete - a function to fire when return box value
    */  
    messageBox: function(type, message, properties, input) {
        properties = Object.extend({
            'textBoxBtnOk': 'OK',
            'textBoxBtnCancel': 'Cancel',
            'textBoxInputPrompt': null,
            'onComplete': Class.empty
        }, properties || {});
 
        this.options.onReturnFunction = properties.onComplete;
 
        if(type == 'alert') {
            this.AlertBtnOk = new Element('input', {
                'id': 'BoxAlertBtnOk',
                'type': 'submit',
                'value': properties.textBoxBtnOk,
                'styles': {
                    'width': '70px'
                }
            });
 
            this.AlertBtnOk.addEvent('click', function() {
                this.options.onReturn = true;
                this.display(0);
            }.bind(this));
 
            this.Content.setProperty('class','BoxAlert').setHTML(message + '<br />');
            this.AlertBtnOk.injectInside(this.Content);
            this.display(1);
        }
        else if(type == 'confirm') {
            this.ConfirmBtnOk = new Element('input', {
                'id': 'BoxConfirmBtnOk',
                'type': 'submit',
                'value': properties.textBoxBtnOk,
                'styles': {
                    'width': '70px'
                }
            });
 
            this.ConfirmBtnCancel = new Element('input', {
                'id': 'BoxConfirmBtnCancel',
                'type': 'submit',
                'value': properties.textBoxBtnCancel,
                'styles': {
                    'width': '70px'
                }
            });
 
            this.ConfirmBtnOk.addEvent('click', function() {
                this.options.onReturn = true;
                this.display(0);
            }.bind(this));
 
            this.ConfirmBtnCancel.addEvent('click', function() {
                this.options.onReturn = false;
                this.display(0);
            }.bind(this));      
 
            this.Content.setProperty('class','BoxConfirm').setHTML(message + '<br />');
            this.ConfirmBtnOk.injectInside(this.Content);
            this.ConfirmBtnCancel.injectInside(this.Content);
            this.display(1);
        }
        else if(type == 'prompt') {
            this.PromptBtnOk = new Element('input', {
                'id': 'BoxPromptBtnOk',
                'type': 'submit',
                'value': properties.textBoxBtnOk,
                'styles': {
                    'width': '70px'
                }
            });
 
            this.PromptBtnCancel = new Element('input', {
                'id': 'BoxPromptBtnCancel',
                'type': 'submit',
                'value': properties.textBoxBtnCancel,
                'styles': {
                    'width': '70px'
                }
            });         
 
            this.PromptInput = new Element('input', {
                'id': 'BoxPromptInput',
                'type': 'text',
                'value': input,
                'styles': {
                    'width': '250px'
                }
            });
 
            this.PromptBtnOk.addEvent('click', function() {
                this.options.onReturn = this.PromptInput.value;
                this.display(0);
            }.bind(this));
 
            this.PromptBtnCancel.addEvent('click', function() {
                this.options.onReturn = false;
                this.display(0);
            }.bind(this));
 
            this.Content.setProperty('class','BoxPrompt').setHTML(message + '<br />');
            this.PromptInput.injectInside(this.Content);
            new Element('br').injectInside(this.Content);
            this.PromptBtnOk.injectInside(this.Content);
            this.PromptBtnCancel.injectInside(this.Content);
            this.display(1);
        }
        else {
            this.options.onReturn = false;
            this.display(0);        
        }
    },
 
    /*
    Property: alert
        Shortcut for alert
 
    Argument:
        properties - see Options in messageBox
    */      
    alert: function(message, properties){
        this.messageBox('alert', message, properties);
    },
 
    /*
    Property: confirm
        Shortcut for confirm
 
    Argument:
        properties - see Options in messageBox
    */
    confirm: function(message, properties){
        this.messageBox('confirm', message, properties);
    },
 
    /*
    Property: prompt
        Shortcut for prompt
 
    Argument:
        properties - see Options in messageBox
    */  
    prompt: function(message, input, properties){
        this.messageBox('prompt', message, properties, input);
    }
});
 
mooPrompter.implement(new Events, new Options);