package org.jenkinsci.plugins.composercolumns.columns;

import java.io.InputStream;

import hudson.Extension;
import hudson.FilePath;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Javi H. Gil
 */
public class VersionColumn extends ListViewColumn {

	@Extension
	public static class DescriptorImpl extends ListViewColumnDescriptor {
		@Override
		public String getDisplayName() {
			return "Composer Version";
		}

		@Override
		public boolean shownByDefault() {
			return false;
		}
	}
	
	@DataBoundConstructor
	public VersionColumn(String columnName) {
		super();
		this.columnName = columnName;
	}
		
	
	
	private String columnName;
	
	@Override
	public String getColumnCaption() {
		return columnName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	
	public JSONObject getComposerJson(FilePath workspace) throws Exception {	

		FilePath path = workspace.child("composer.json");
		
		InputStream is = path.read();
        String jsonTxt = IOUtils.toString( is );
        is.close();

        JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
        return json;
	}	

	
	public String getVersion(AbstractProject job) {
		try {
			AbstractBuild build = job.getLastBuild();
			FilePath workspace = build.getWorkspace();
			
			JSONObject json= this.getComposerJson(workspace);
			return json.get("version").toString();
		} catch (Exception e) {
			return "";
		}				
	}
	
	public boolean isDev(AbstractProject job) {
		String version = this.getVersion(job);		
		return version.endsWith("-dev");		
	}
}
