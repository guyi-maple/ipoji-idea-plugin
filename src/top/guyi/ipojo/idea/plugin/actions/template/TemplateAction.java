package top.guyi.ipojo.idea.plugin.actions.template;

public class TemplateAction<C> {

    private String file;
    private String error;
    private C content;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public C getContent() {
        return content;
    }

    public void setContent(C content) {
        this.content = content;
    }
}
