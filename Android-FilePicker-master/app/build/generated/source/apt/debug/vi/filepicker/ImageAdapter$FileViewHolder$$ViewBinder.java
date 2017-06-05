// Generated code from Butter Knife. Do not modify!
package vi.filepicker;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ImageAdapter$FileViewHolder$$ViewBinder<T extends vi.filepicker.ImageAdapter.FileViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624073, "field 'imageView'");
    target.imageView = finder.castView(view, 2131624073, "field 'imageView'");
  }

  @Override public void unbind(T target) {
    target.imageView = null;
  }
}
